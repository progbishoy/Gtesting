  /*Copyright (C) 2014  JD Software, Inc.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
  */
package com.jd.survey.web.settings;

  import com.jd.survey.domain.security.User;
  import com.jd.survey.domain.settings.*;
  import com.jd.survey.service.security.SecurityService;
  import com.jd.survey.service.security.UserService;
  import com.jd.survey.service.settings.SurveySettingsBankService;
  import com.jd.survey.service.settings.SurveySettingsService;
  import org.apache.commons.logging.Log;
  import org.apache.commons.logging.LogFactory;
  import org.owasp.validator.html.AntiSamy;
  import org.owasp.validator.html.CleanResults;
  import org.owasp.validator.html.Policy;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.context.MessageSource;
  import org.springframework.context.i18n.LocaleContextHolder;
  import org.springframework.security.access.annotation.Secured;
  import org.springframework.stereotype.Controller;
  import org.springframework.ui.Model;
  import org.springframework.validation.BindingResult;
  import org.springframework.web.bind.annotation.*;
  import org.springframework.web.util.UriUtils;
  import org.springframework.web.util.WebUtils;

  import javax.servlet.http.HttpServletRequest;
  import javax.validation.Valid;
  import java.io.UnsupportedEncodingException;
  import java.security.Principal;
  import java.util.*;


  @RequestMapping("/settings/questionsBank")
  @Controller
  public class QuestionBankController {
      private static final Log log = LogFactory.getLog(QuestionBankController.class);	
      private static final String EXTREMELY_UNSATISFIED_LABEL = "extremely_unsatisfied_label";
      private static final String UNSATISFIED_LABEL = "unsatisfied_label";
      private static final String NEUTRAL_LABEL = "neutral_label";
      private static final String SATISFIED_LABEL = "satisfied_label";
      private static final String EXTREMELY_SATISFIED_LABEL = "extremely_satisfied_label";
      private static final String  POLICY_FILE_LOCATION="/antisamy-tinymce-1-4-4.xml";
      
      private static short size;
  
          
      @Autowired	private MessageSource messageSource;
      @Autowired	private SurveySettingsBankService surveySettingsService;
      @Autowired	private UserService userService;
      @Autowired	private SecurityService securityService;
  
      @Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
      @RequestMapping( params = "create", produces = "text/html")
      public String createQuestion(
                                  Principal principal,	
                                  Model uiModel,
                                  HttpServletRequest httpServletRequest) {
          log.info("createForm(): handles param form");
          try {
              String login = principal.getName();
              User user = userService.user_findByLogin(login);

              //Check if the user is authorized

              //User user = userService.user_findByLogin(principal.getName());
              //SurveyDefinitionPage surveyDefinitionPage =  surveySettingsService.surveyDefinitionPage_findById(surveyDefinitionPageId);
              QuestionBank question = new QuestionBank();
              populateEditForm(uiModel, question, user);
  
              return "settings/questionsBank/create";
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              throw (new RuntimeException(e));
          }	
      }
  
      
      @Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
      @RequestMapping(method = RequestMethod.POST, produces = "text/html")
      public String create(@RequestParam(value = "_proceed", required = false) String proceed,
                          @Valid QuestionBank question, 
                          BindingResult bindingResult, 
                          Principal principal,	
                          Model uiModel, 
                          HttpServletRequest httpServletRequest) {
          log.info("create(): handles " + RequestMethod.POST.toString());
          
          try {
              String login = principal.getName();
              User user = userService.user_findByLogin(login);
              //SurveyDefinitionPage surveyDefinitionPage = surveySettingsService.surveyDefinitionPage_findById(surveyDefinitionPageId); 
              //Check if the user is authorized
              

              //User user = userService.user_findByLogin(principal.getName());
              if(proceed != null){
                  if (bindingResult.hasErrors()) {
                      populateEditForm(uiModel, question, user);
                      return "settings/questionsBank/create";
                  }
                  if(question.getQuestionGrade()==null){
                	     populateEditForm(uiModel, question, user);
                         return "settings/questionsBank/create"; 
                  }
                  if (!surveySettingsService.question_ValidateDateRange(question)){
                      populateEditForm(uiModel, question,user);
                      bindingResult.rejectValue("dateMinimum", "date_format_validation_range");
                      return "settings/questionsBank/create";	
                  }	
                  //validate Double min max	
                  if (!surveySettingsService.question_ValidateMinMaxDoubleValues(question)){
                      populateEditForm(uiModel, question,user);
                      bindingResult.rejectValue("decimalMinimum", "field_min_invalid");
                      return "settings/questionsBank/create";	
                  }	
                  //validate Integer min max	
                  if (!surveySettingsService.question_ValidateMinMaxValues(question)){
                      populateEditForm(uiModel, question,user);
                      bindingResult.rejectValue("integerMinimum", "field_min_invalid");
                      return "settings/questionsBank/create";	
                  }
                  //User u=new User();
                  //u.setId(user.getId());
                  question.setCreatedBy(user);
                  question.setCreatedDate(new Date());
                  question.setStatus(QuestionBankStatus.NEW);
                  if(!question.getOtherTag().trim().equals(""))
                  {
                      question.setQuestionTag(surveySettingsService.questionBankAddTag(question.getOtherTag()));
                      if(user.getDepartments().size()==1)
                      {
                          Department userDep=user.getDepartments().first();
                          userDep.getTags().add(question.getQuestionTag());
                          surveySettingsService.department_merge(userDep);
                      }
                  }
                  if (question.getType().getIsRating()) {
                      SortedSet<QuestionBankOption> options = new TreeSet<QuestionBankOption>();
                      options.add(new QuestionBankOption(question, (short)1 ,"1",messageSource.getMessage(EXTREMELY_UNSATISFIED_LABEL, null, LocaleContextHolder.getLocale())));
                      options.add(new  QuestionBankOption(question, (short)2 ,"2",messageSource.getMessage(UNSATISFIED_LABEL, null, LocaleContextHolder.getLocale())));
                      options.add(new  QuestionBankOption(question, (short)3 ,"3",messageSource.getMessage(NEUTRAL_LABEL, null, LocaleContextHolder.getLocale())));
                      options.add(new  QuestionBankOption(question, (short)4 ,"4",messageSource.getMessage(SATISFIED_LABEL, null, LocaleContextHolder.getLocale())));
                      options.add(new  QuestionBankOption(question, (short)5 ,"5",messageSource.getMessage(EXTREMELY_SATISFIED_LABEL, null, LocaleContextHolder.getLocale())));
                      question =surveySettingsService.question_merge(question,options);	
                  }
                  
  //				if (question.getPublishToSocrata().equals(true)){
  //					bindingResult.rejectValue("socrataColumnName", "field_min_invalid");
  //					return "settings/questionsBank/create";	
  //					}
                  
                  else {
                      
                      Policy questionTextPolicy = Policy.getInstance(this.getClass().getResource(POLICY_FILE_LOCATION));
                      AntiSamy emailAs = new AntiSamy();
                      CleanResults crQuestionText = emailAs.scan(question.getQuestionText(), questionTextPolicy);
                      question.setQuestionText(crQuestionText.getCleanHTML());
  
                      CleanResults crQuestionAnswer = emailAs.scan(question.getQuestionAnswer(), questionTextPolicy);
                      question.setQuestionAnswer(crQuestionAnswer.getCleanHTML());
                      
                      Policy questionTipPolicy = Policy.getInstance(this.getClass().getResource(POLICY_FILE_LOCATION));
                      AntiSamy completedSurveyAs = new AntiSamy();
                      CleanResults crQuestionTip = completedSurveyAs.scan(question.getTip(), questionTipPolicy);
                      question.setTip(crQuestionTip.getCleanHTML());
                      
                      question =surveySettingsService.question_merge(question);
                  
                  } 	
                  uiModel.asMap().clear();
                  uiModel.addAttribute("question",question);
                  return "settings/questionsBank/saved";
              }
  
              else {
                  return "redirect:/settings/questionsBank/saved";
              }
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              throw (new RuntimeException(e));
          }
  
      }
  
      @Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
      @RequestMapping(value = "/{id}", produces = "text/html")
      public String show(@PathVariable("id") Long id,
                          HttpServletRequest httpServletRequest,
                          Principal principal,	
                          Model uiModel) {
          log.info("show(): id=" + id);
          try {
              String login = principal.getName();
              User user = userService.user_findByLogin(login);
              //SurveyDefinitionPage surveyDefinitionPage = surveySettingsService.surveyDefinitionPage_findById(id); 
              //Check if the user is authorized
            QuestionBank q =surveySettingsService.question_findById(id);
              List<QuestionBank> questions=new ArrayList<QuestionBank>();
              questions.add(q);
              uiModel.addAttribute("TagsList",surveySettingsService.tags_findByDepartments(user));
              uiModel.addAttribute("questions", questions);
              uiModel.addAttribute("question", q);
              uiModel.addAttribute("itemId", id);
              return "settings/questionsBank/show";   
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              throw (new RuntimeException(e));
          }
      }
      
      @Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
      @RequestMapping(value = "/lookup", produces = "text/html")
      public String lookup(Principal principal,
                           @RequestParam(value = "page", required = false) Integer page,
                           @RequestParam(value = "size", required = false ) Integer size,
                           QuestionBank question,
                            Model uiModel,
                            HttpServletRequest httpServletRequest) {
          //Set<QuestionBank> questions=surveySettingsService.question_findAll();
          //uiModel.addAttribute("",questions);
          String login = principal.getName();
          User user = userService.user_findByLogin(login);
          uiModel.addAttribute("TagsList",surveySettingsService.tags_findByDepartments(user));
          uiModel.addAttribute("DifList",QuestionDifficultyLevel.values());
          uiModel.addAttribute("StatusList",QuestionBankStatus.values());
          uiModel.addAttribute("QuestionTypeList",QuestionType.values());







              //int sizeNo = size == null ? 10 : size.intValue();
              //final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
              //Set<QuestionBank> Qall =surveySettingsService.question_findAll();
              //uiModel.addAttribute("questions", Qall);
              //float nrOfPages = (float) surveySettingsService.question_findAll().size()/ sizeNo;
              //uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));



          return "settings/questionsBank/lookup";
          
      }

      @Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
      @RequestMapping(value = "/search", produces = "text/html")
      public String search(Principal principal,
                           @RequestParam(value = "page", required = false) Integer page,
                           @RequestParam(value = "size", required = false ) Integer size,
                           QuestionBank question,
                           Model uiModel,
                           HttpServletRequest httpServletRequest) {

          String login = principal.getName();
          User user = userService.user_findByLogin(login);
          Set<Tags> userTags=surveySettingsService.tags_findByDepartments(user);
          uiModel.addAttribute("TagsList",userTags);
          uiModel.addAttribute("DifList",QuestionDifficultyLevel.values());
          uiModel.addAttribute("StatusList",QuestionBankStatus.values());
          uiModel.addAttribute("QuestionTypeList",QuestionType.values());
         

          
          int sizeNo = size == null ? 10 : size.intValue();
          final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
          if(question.getQuestionTag()!=null){
          Set<QuestionBank> Qall=surveySettingsService.question_search(userTags,question,0, 0);
          uiModel.addAttribute("questions",Qall );
          float nrOfPages = (float)Qall.size() / sizeNo;
          uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
          uiModel.addAttribute("question",question);
          }

          return "settings/questionsBank/lookup";
      }
  
      @Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
      @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
      public String update(@RequestParam(value = "_proceed", required = false) String proceed,
                          @Valid QuestionBank question,
                          BindingResult bindingResult,
                          Principal principal,	
                          Model uiModel, 
                          HttpServletRequest httpServletRequest) {
          log.info("update(): handles PUT");
          try{
              //User user = userService.user_findByLogin(principal.getName());
              String login = principal.getName();
              User user = userService.user_findByLogin(login);
              
              //SurveyDefinitionPage surveyDefinitionPage = surveySettingsService.surveyDefinitionPage_findById(surveyDefinitionPageId); surveySettingsService.question_findById(question.getId()).getPage().getSurveyDefinition().getId()
              //Check if the user is authorized

              if(proceed != null ){
                  if (bindingResult.hasErrors()) {
                      populateEditForm(uiModel, question,user);
                      log.info("-------------------------------------------" +bindingResult.getFieldErrors().toString());
                      return "settings/questionsBank/update";
                  }
                  if (!surveySettingsService.question_ValidateDateRange(question)){
                      populateEditForm(uiModel, question,user);
                      bindingResult.rejectValue("dateMinimum", "date_format_validation_range");
                      return "settings/questionsBank/update";	
                  }	
                  if (!surveySettingsService.question_ValidateMinMaxDoubleValues(question) ){
                      populateEditForm(uiModel, question,user);
                      bindingResult.rejectValue("decimalMinimum", "field_min_invalid");
                      return "settings/questionsBank/update";	
                  }	
                  if (!surveySettingsService.question_ValidateMinMaxValues(question) ){
                      populateEditForm(uiModel, question,user);
                      bindingResult.rejectValue("integerMinimum", "field_min_invalid");
                      return "settings/questionsBank/update";	
                  }
                  //User u=new User();
                  //u.setId(user.getId());

                  question.setModifiedBy(user);
                  question.setModifiedDate(new Date());
                  if(!question.getOtherTag().trim().equals(""))
                  {
                      question.setQuestionTag(surveySettingsService.questionBankAddTag(question.getOtherTag()));
                  }
                  if (question.getSuportsOptions()){
                      //If user wants to modify and existent question without options to Rating type, then use the default values
                      int NumberOfQuestionBankOptions = 0;
                      Set<QuestionBankOption> qOpts = surveySettingsService.questionOption_findByQuestionId(question);
                      for (QuestionBankOption q : qOpts){
                          NumberOfQuestionBankOptions++;
                      }
                      if ((question.getType().toString()=="SMILEY_FACES_RATING" || question.getType().toString()=="STAR_RATING") && NumberOfQuestionBankOptions != 5){
                          log.info("Removing QuestionBank Options since the amount of QuestionBanks Options for Rating Type cannot be longer than 5 Qoptions");
                          surveySettingsService.questionOption_removeQuestionOptionsByQuestionId(question.getId());
                          SortedSet<QuestionBankOption> options = new TreeSet<QuestionBankOption>();
                          options.add(new  QuestionBankOption(question, (short)1 ,"1",messageSource.getMessage(EXTREMELY_UNSATISFIED_LABEL, null, LocaleContextHolder.getLocale())));
                          options.add(new  QuestionBankOption(question, (short)2 ,"2",messageSource.getMessage(UNSATISFIED_LABEL, null, LocaleContextHolder.getLocale())));
                          options.add(new  QuestionBankOption(question, (short)3 ,"3",messageSource.getMessage(NEUTRAL_LABEL, null, LocaleContextHolder.getLocale())));
                          options.add(new  QuestionBankOption(question, (short)4 ,"4",messageSource.getMessage(SATISFIED_LABEL, null, LocaleContextHolder.getLocale())));
                          options.add(new  QuestionBankOption(question, (short)5 ,"5",messageSource.getMessage(EXTREMELY_SATISFIED_LABEL, null, LocaleContextHolder.getLocale())));
                          //Adding default values to Rating Type QuestionBank
                          log.info("Adding default values to Rating Type QuestionBank");
                          question =surveySettingsService.question_merge(question,options);
                          uiModel.asMap().clear();
                          return "settings/questionsBank/saved";
                      }
                      else{
                          Policy questionTextPolicy = Policy.getInstance(this.getClass().getResource(POLICY_FILE_LOCATION));
                          AntiSamy emailAs = new AntiSamy();
                          CleanResults crQuestionText = emailAs.scan(question.getQuestionText(), questionTextPolicy);
                          question.setQuestionText(crQuestionText.getCleanHTML());
                          CleanResults crQuestionAnswer = emailAs.scan(question.getQuestionAnswer(), questionTextPolicy);
                          question.setQuestionAnswer(crQuestionAnswer.getCleanHTML());
                          Policy questionTipPolicy = Policy.getInstance(this.getClass().getResource(POLICY_FILE_LOCATION));
                          AntiSamy completedSurveyAs = new AntiSamy();
                          CleanResults crQuestionTip = completedSurveyAs.scan(question.getTip(), questionTipPolicy);
                          question.setTip(crQuestionTip.getCleanHTML());
                          
                          question =surveySettingsService.question_merge(question);
                          uiModel.asMap().clear();
                          return "settings/questionsBank/saved";
                      }
                  }
                  
                  question =surveySettingsService.question_merge(question);
                  uiModel.asMap().clear();
                  return "settings/questionsBank/saved";
              
              }else{
                  return "redirect:/settings/questionsBank/lookup";
              }
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              throw (new RuntimeException(e));
          }
      }
  
      @Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
      @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
      public String updateForm(@PathVariable("id") Long id,
                              Principal principal,	
                              Model uiModel) {
          log.info("updateForm(): id=" + id);
          try{
              User user = userService.user_findByLogin(principal.getName());
              populateEditForm(uiModel, surveySettingsService.question_findById(id),user);
              uiModel.addAttribute("TagsList",surveySettingsService.tags_findByDepartments(user));
              return "settings/questionsBank/update";
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              throw (new RuntimeException(e));
          }
      }
  
      
      @Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
      @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
      public String delete(@PathVariable("id") Long id, 
                           Principal principal,
                           Model uiModel, 
                           HttpServletRequest httpServletRequest) {
          log.info("delete(): id=" + id);
          try {
              QuestionBank question = surveySettingsService.question_findById(id);
              String login = principal.getName();
              User user = userService.user_findByLogin(login);
              //SurveyDefinitionPage surveyDefinitionPage = surveySettingsService.surveyDefinitionPage_findById(surveyDefinitionPageId); 
              //Check if the user is authorized

              
              surveySettingsService.question_remove(id);
              uiModel.asMap().clear();
              return "redirect:/settings/questionsBank/lookup";
  
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              throw (new RuntimeException(e));
          }
      }
  
  
  
      void populateEditForm(Model uiModel, QuestionBank question, User user) {
          log.info("populateEditForm()");
          try{
              uiModel.addAttribute("TagsList",surveySettingsService.tags_findByDepartments(user));
              uiModel.addAttribute("question", question);
              uiModel.addAttribute("regularExpressions", surveySettingsService.regularExpression_findAll());
              uiModel.addAttribute("questionOptions", question.getType());
              uiModel.addAttribute("datasets", surveySettingsService.dataSet_findAll());



          } catch (Exception e) {
              log.error(e.getMessage(),e);
              throw (new RuntimeException(e));
          }
  
      }
  
      String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
          log.info("encodeUrlPathSegment()");
          try{
              String enc = httpServletRequest.getCharacterEncoding();
              if (enc == null) {
                  enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
              }
              try {
                  pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
              } catch (UnsupportedEncodingException uee) {log.error(uee);}
              return pathSegment;
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              throw (new RuntimeException(e));
          }
      }
  
  
  
  
  
  
  
  
  
      @ExceptionHandler(RuntimeException.class)
      public String handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
          log.error(ex);
          ex.printStackTrace();
          log.error("redirect to /uncaughtException");
          return "redirect:/uncaughtException";
      }

      public BindingResult validate(BindingResult bindingResult)
      {

          return bindingResult;
      }

  
  }
