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

  import com.jd.survey.domain.settings.QuestionBank;
  import com.jd.survey.domain.settings. QuestionBankOption ;
  import com.jd.survey.service.security.SecurityService;
  import com.jd.survey.service.security.UserService;
  import com.jd.survey.service.settings.SurveySettingsBankService;
  import org.apache.commons.logging.Log;
  import org.apache.commons.logging.LogFactory;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.security.access.annotation.Secured;
  import org.springframework.stereotype.Controller;
  import org.springframework.ui.Model;
  import org.springframework.validation.BindingResult;
  import org.springframework.web.bind.annotation.*;
  import org.springframework.web.util.UriUtils;
  import org.springframework.web.util.WebUtils;

  import javax.servlet.http.HttpServletRequest;
  import java.io.UnsupportedEncodingException;
  import java.security.Principal;
  import java.util.SortedSet;


  @RequestMapping("/settings/questionBankOptions")
  @Controller
  public class QuestionBankOptionsController {
      private static final Log log = LogFactory.getLog(QuestionBankOptionsController.class);
      
      private static final int EMPTY_OPTIONS_COUNT = 10;
      
      @Autowired	private SurveySettingsBankService surveySettingsService;
      @Autowired	private UserService userService;
      @Autowired	private SecurityService securityService;
      
      @Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
      @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
      public String updateForm(@PathVariable("id") Long questionId, 
                              Principal principal,	
                              Model uiModel) {
          log.info("updateForm(): questionId=" + questionId);
          try{
              QuestionBank question = surveySettingsService.question_findById(questionId);
              SortedSet< QuestionBankOption > options =  question.getOptions();
              
              for (int i =1; i<=EMPTY_OPTIONS_COUNT; i++){
                  options.add(new  QuestionBankOption (question,(short) (question.getOptions().size() + i)));
              }
              question.setOptions(options);
              uiModel.addAttribute("question", question);
              return "settings/questionBankOptions/update";
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              throw (new RuntimeException(e));
          }
      }
      
      @Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
      @RequestMapping(method = RequestMethod.POST, produces = "text/html")
      public String createPost(QuestionBank question,
                               BindingResult bindingResult,
                               @RequestParam(value="_proceed", required = false) String proceed,
                               Principal principal,	
                               Model uiModel, 
                               HttpServletRequest httpServletRequest) {
          log.info("create(): handles " + RequestMethod.POST.toString());
          try {
              log.info("-------------------------------------------------");
              String login = principal.getName();

              if(proceed != null){
                  boolean hasNoOptions =true;
                  boolean isValid=true;
                  int i=0;
                  for ( QuestionBankOption   QuestionBankOption  :question.getOptionsList2()) {
                      
                      //if the value or text is not null and not a blank string
                      if (( QuestionBankOption .getValue() != null	&&	 QuestionBankOption .getText() != null)
                          &&
                          ( QuestionBankOption .getValue().trim().length() > 0 ||	 QuestionBankOption .getText().trim().length() > 0)	
                          ){
                              hasNoOptions = false;
                              //validate the length
                              if ( QuestionBankOption .getValue().trim().length() ==0	||
                                       QuestionBankOption .getValue().trim().length() > 5 ){
                                  bindingResult.rejectValue("optionsList2["+ i +"].value", "nullvalue");
                                  isValid= false;
                                  
                              }
                              //validate the length
                              if ( QuestionBankOption .getText().trim().length() ==0 ||
                                       QuestionBankOption .getText().trim().length() > 250){
                                  bindingResult.rejectValue("optionsList2["+ i +"].text", "nullvalue");
                                  isValid= false;
                              }
                      }
                      i++;
                  }
      
                  if (hasNoOptions) {
                      isValid= false;
                      bindingResult.rejectValue("optionsList2["+ 0 +"].value", "nullValueEntered");
                      //re-populate the place holders for options
                      for (int j =1; j<=EMPTY_OPTIONS_COUNT; j++){
                          question.getOptions().add(new  QuestionBankOption (question,(short) (question.getOptions().size() + j)));
                      }
                  }
  
                  if (!isValid){
                      return "settings/questionBankOptions/update";
                  }
                  else{
                      QuestionBank q= surveySettingsService.question_updateOptions(question);
                      return "settings/questionBankOptions/saved";
                  }
              }
              
              //cancel button handler
              else{
                  QuestionBank q= surveySettingsService.question_updateOptions(question);
                  return "redirect:/settings/questionBankOptions/saved";
              }
              
  
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
          log.error("redirect to /uncaughtException");
          return "redirect:/uncaughtException";
      }
  
  }
