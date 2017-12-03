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
package com.jd.survey.service.settings;

  import au.com.bytecode.opencsv.CSVReader;
  import com.jd.survey.dao.interfaces.settings.*;
  import com.jd.survey.dao.interfaces.survey.ReportDAO;
  import com.jd.survey.dao.interfaces.survey.SurveyDAO;
  import com.jd.survey.domain.security.User;
  import com.jd.survey.domain.settings.*;
  import com.jd.survey.service.email.MailService;
  import org.apache.commons.logging.Log;
  import org.apache.commons.logging.LogFactory;
  import org.apache.velocity.VelocityContext;
  import org.apache.velocity.app.Velocity;
  import org.joda.time.DateTime;
  import org.joda.time.Months;
  import org.joda.time.Weeks;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.context.MessageSource;
  import org.springframework.context.i18n.LocaleContextHolder;
  import org.springframework.dao.DataAccessException;
  import org.springframework.stereotype.Service;
  import org.springframework.transaction.annotation.Transactional;

  import java.io.StringWriter;
  import java.util.*;


  @Transactional(readOnly = true)
  @Service("SurveySettingsBankService")
  public class SurveySettingsBankService {
  
      private static final Log log = LogFactory.getLog(SurveySettingsBankService.class);	
  
       
      @Autowired	private MessageSource messageSource;
      @Autowired	private DepartmentDAO departmentDAO;
      @Autowired	private SurveyDefinitionDAO surveyDefinitionDAO;
      @Autowired	private SurveyDefinitionPageDAO surveyDefinitionPageDAO;
      @Autowired	private QuestionBankOptionDAO questionOptionDAO;
      @Autowired	private QuestionRowLabelDAO rowLabelDAO;
      @Autowired	private QuestionColumnLabelDAO columnLabelDAO;
  
      @Autowired	private QuestionRowLabelDAO questionRowLabelDAO;
      @Autowired	private QuestionColumnLabelDAO questionColumnLabelDAO;
      @Autowired	private QuestionBankDAO questionDAO;
      
      @Autowired	private DataSetDAO dataSetDAO;
      @Autowired	private DataSetItemDAO dataSetItemDAO;
      @Autowired	private VelocityTemplateDAO velocityTemplateDAO;
      @Autowired  private RegularExpressionDAO regularExpressionDAO;
  
      @Autowired private SurveyDAO surveyDAO; 
      @Autowired private InvitationDAO invitationDAO;
      @Autowired private MailService mailService;
      @Autowired private SectorDAO sectorDAO;
      @Autowired private SurveyTemplateDAO surveyTemplateDAO;
      @Autowired private DayDAO dayDAO;
      @Autowired private ReportDAO reportDAO;
      @Autowired private TagsDAO tagsDAO;
      @Autowired private SurveyTagsDAO surveyTagsDAO;
  
     
  
  
      private static final String INVITATION_EMAIL_TITLE="invitation_email_title";
      private static final String INVITEE_FULLNAME_PARAMETER_NAME="invitee_fullname_parameter_name";
      private static final String SURVEY_NAME="survey_name";
      private static final String INVITE_FILL_SURVEY_LINK_PARAMETER_NAME="invite_fill_survey_link_parameter_name";
      private static final String INVITE_FILL_SURVEY_LINK_LABEL="invite_fill_survey_link_label";
      private static final String INTERNAL_SITE_BASE_URL="internal_site_base_url";
      private static final String EXTERNAL_SITE_BASE_URL="external_site_base_url";
  
  
  
      public Set<Department> department_findAll() throws DataAccessException {
          return departmentDAO.findAll();
      }
      public Set<Department> department_findAll(int startResult,int maxRows) throws DataAccessException {
          return departmentDAO.findAll(startResult, maxRows);
      }
      public Set<Department> department_findAll( User user) throws DataAccessException {
  
          if (user.isAdmin()){
              return departmentDAO.findAll();
          }else{
              return departmentDAO.getUserDepartments(user.getLogin(), -1, -1);
  
          }
      }
  
      public Long department_getCount() {
          return departmentDAO.getCount();
      }
      public Department department_findById(Long id) {
          return departmentDAO.findById(id);
      }
      public Department department_findByName(String name) {
          return departmentDAO.findByName(name);
      }
  
  
      @Transactional(readOnly = false)
      public Department department_merge(Department department) {
          return departmentDAO.merge(department);
      }
  
      @Transactional(readOnly = false)
      public void department_remove(Department department) {
          departmentDAO.remove(department);
      }
  
  
      
      public boolean question_ValidateMinMaxDoubleValues(QuestionBank question) {
          if (question.getType() == QuestionType.CURRENCY_INPUT ||
                  question.getType()== QuestionType.DECIMAL_INPUT){
              if(question.getDecimalMinimum() != null &&
                      question.getDecimalMaximum() != null	&&
                      question.getDecimalMinimum().doubleValue() >= question.getDecimalMaximum().doubleValue()){
                  return false;
              }
          }
          return true;
      }
  
      public boolean question_ValidateMinMaxValues(QuestionBank question) {
          if (question.getType()== QuestionType.INTEGER_INPUT ||
                  question.getType()== QuestionType.SHORT_TEXT_INPUT ||
                  question.getType()== QuestionType.LONG_TEXT_INPUT ||
                  question.getType()== QuestionType.HUGE_TEXT_INPUT){
              if(		question.getIntegerMinimum() != null &&
                      question.getIntegerMaximum() != null &&
                      question.getIntegerMinimum() >= question.getIntegerMaximum()){
                  return false;
              }
          }
          return true;
      }
  
  
  
  
      public boolean question_ValidateDateRange (QuestionBank question){
          if (question.getDateMinimum() != null &&
                  question.getDateMaximum() != null &&
                  question.getDateMinimum().after(question.getDateMaximum())){
              return false;
          }
          return true;
      }
  
  
  
      public Set<SurveyDefinition> surveyDefinition_findAllInternal(User user) throws DataAccessException{
          if (user.isAdmin()){
              return surveyDefinitionDAO.findAllInternal(-1, -1);
          }
          else {
              return surveyDefinitionDAO.findAllInternal(user.getLogin(),-1, -1);
          }
      }
  
      public Set<SurveyDefinition> surveyDefinition_findAllInternal(User user, int startResult, int maxRows) throws DataAccessException{
          if (user.isAdmin()){
              return surveyDefinitionDAO.findAllInternal(startResult, maxRows);
          }
          else {
              return surveyDefinitionDAO.findAllInternal(user.getLogin(),startResult, maxRows);
          }
      }
  
  
      public Set<SurveyDefinition> surveyDefinition_findAllCompletedInternal(User user) throws DataAccessException{
          if (user.isAdmin()){
              return surveyDefinitionDAO.findAllCompletedInternal(-1, -1);
          }
          else {
              return surveyDefinitionDAO.findAllCompletedInternal(user.getLogin(),-1, -1);
          }
      }
  
      public Set<SurveyDefinition> surveyDefinition_findAllCompletedInternal(User user,int startResult, int maxRows) throws DataAccessException{
          if (user.isAdmin()){
              return surveyDefinitionDAO.findAllCompletedInternal(startResult, maxRows);
          }
          else {
              return surveyDefinitionDAO.findAllCompletedInternal(user.getLogin(), startResult, maxRows);
          }
      }
  
  
      public Set<SurveyDefinition> surveyDefinition_findAllPublishedInternal(User user) throws DataAccessException{
          if (user.isAdmin()){
              return surveyDefinitionDAO.findAllPublishedInternal(-1, -1);
          }
          else {
              return surveyDefinitionDAO.findAllPublishedInternal(user.getLogin(),-1, -1);
          }
      }
  
      public Set<SurveyDefinition> surveyDefinition_findAllPublishedInternal(User user,int startResult, int maxRows) throws DataAccessException{
          if (user.isAdmin()){
              return surveyDefinitionDAO.findAllPublishedInternal(startResult, maxRows);
          }
          else {
              return surveyDefinitionDAO.findAllPublishedInternal(user.getLogin(), startResult, maxRows);
          }
      }
  
  
  
      public Set<SurveyDefinition> surveyDefinition_findAllPublishedExternal(User user) throws DataAccessException{
          if (user.isAdmin()){
              Set<SurveyDefinition> surveyDefinitions  = surveyDefinitionDAO.findAllPublishedInternal(-1, -1);
              return surveyDefinitions;
          }
          if (user.isSurveyAdmin()){
              return surveyDefinitionDAO.findAllPublishedInternal(user.getLogin(),-1, -1);
          }
          else {
              return surveyDefinitionDAO.findAllPublishedExternal(user.getLogin(),-1, -1);
          }
      }
  
  
      public Set<SurveyDefinition> surveyDefinition_findAllPublishedExternal(User user,int startResult, int maxRows) throws DataAccessException{
          if (user.isAdmin()){
              return surveyDefinitionDAO.findAllPublishedInternal(startResult, maxRows);
          }
          if (user.isSurveyAdmin()){
              return surveyDefinitionDAO.findAllPublishedInternal(user.getLogin(),startResult, maxRows);
          }
          else {
              return surveyDefinitionDAO.findAllPublishedExternal(user.getLogin(),startResult, maxRows);
          }
      }
  
  
  
  
      public Set<SurveyDefinition> surveyDefinition_findAllPublishedPublic() throws DataAccessException{
          return surveyDefinitionDAO.findAllPublishedPublic();
      }
  
  
      public Set<SurveyDefinition> surveyDefinition_findAllPublishedPublic(int startResult, int maxRows) throws DataAccessException{
          return surveyDefinitionDAO.findAllPublishedPublic(startResult, maxRows);
      }
  
  
  
  
  
  
  
  
  
      public Long surveyDefinition_getCount() {
          return surveyDefinitionDAO.getCount();
      }
      public SurveyDefinition surveyDefinition_findById(Long id) {
          return surveyDefinitionDAO.findById(id);
      }
  
  
      public SurveyDefinition surveyDefinition_findByIdEager(Long id) {
          return surveyDefinitionDAO.findByIdEager(id);
      }
  
      //	@Transactional(readOnly = false)
      //	public SurveyDefinition surveyDefinition_merge(SurveyDefinition surveyDefinition) {
      //		surveyDefinition.setDepartment(departmentDAO.findById(surveyDefinition.getDepartment().getId()));
      //		return surveyDefinitionDAO.merge(surveyDefinition);}
  
      @Transactional(readOnly = false)
      public SurveyDefinition surveyDefinition_merge(SurveyDefinition surveyDefinition) {
  
  
  
  
          if (surveyDefinition.getId() == null){
              surveyDefinition.setDepartment(departmentDAO.findById(surveyDefinition.getDepartment().getId()));
              SurveyDefinition dbSurveyDefinition = surveyDefinitionDAO.merge(surveyDefinition);
  
              for (SurveyTags surveyTag:surveyDefinition.getSurveyTags()) {
                  System.out.println("Glgnh0f"+surveyTag.getId());
  
                  Tags Tag =surveyTag.getTag();
                  surveyTag.setSurveyDefinition(dbSurveyDefinition);
                  surveyTag.setTag(Tag);
  
                  surveyTagsDAO.merge(surveyTag);
  
              }
              ///Glgnhof Added Code
              dbSurveyDefinition.setMainCategory(surveyDefinition.getMainCategory());
  
  
  
  
              dbSurveyDefinition.setTechnology(surveyDefinition.getTechnology());
              dbSurveyDefinition.setLevel(surveyDefinition.getLevel());
              dbSurveyDefinition.setDifficultyLevel(surveyDefinition.getDifficultyLevel());
              dbSurveyDefinition.setNumericalDegree(surveyDefinition.getNumericalDegree());
              surveyDefinition.setDepartment(departmentDAO.findById(surveyDefinition.getDepartment().getId()));
  
              SortedSet<SurveyDefinitionPage> sall= new TreeSet<SurveyDefinitionPage>();
              
              
  
  
              return surveyDefinitionDAO.merge(dbSurveyDefinition);
  
  
          }
          return surveyDefinitionDAO.merge(surveyDefinition);
  
      }
  
      @Transactional(readOnly = false)
      public SurveyDefinition generateQuestionsFromTags(SurveyDefinition surveyDefinition) {
  
          /*
              SurveyDefinitionPage ss0= new SurveyDefinitionPage();
              SurveyDefinitionPage ss1= new SurveyDefinitionPage();
              SurveyDefinitionPage ss2= new SurveyDefinitionPage();
              SurveyDefinitionPage ss3= new SurveyDefinitionPage();
              SurveyDefinitionPage ss4= new SurveyDefinitionPage();
  
              ss0.setSurveyDefinition(dbSurveyDefinition);
              ss1.setSurveyDefinition(dbSurveyDefinition);
              ss2.setSurveyDefinition(dbSurveyDefinition);
              ss3.setSurveyDefinition(dbSurveyDefinition);
              ss4.setSurveyDefinition(dbSurveyDefinition);
  
              ss0.setTitle(dbSurveyDefinition.getMainCategory().toString());
              ss1.setTitle(dbSurveyDefinition.getTechnology().toString());
              ss2.setTitle(dbSurveyDefinition.getLevel().toString());
              ss3.setTitle(dbSurveyDefinition.getDifficultyLevel().toString());
              ss4.setTitle(dbSurveyDefinition.getNumericalDegree().toString());
  
              ss0.setInstructions(dbSurveyDefinition.getMainCategory().toString());
              ss1.setInstructions(dbSurveyDefinition.getTechnology().toString());
              ss2.setInstructions(dbSurveyDefinition.getLevel().toString());
              ss3.setInstructions(dbSurveyDefinition.getDifficultyLevel().toString());
              ss4.setInstructions(dbSurveyDefinition.getNumericalDegree().toString());
  
              ss0.setOrder((short)1);
              ss1.setOrder((short)2);
              ss2.setOrder((short)3);
              ss3.setOrder((short)4);
              ss4.setOrder((short)5);
  
  
              SurveyDefinitionPage newss0=surveyDefinitionPageDAO.store(ss0);
              SurveyDefinitionPage newss1=surveyDefinitionPageDAO.store(ss1);
              SurveyDefinitionPage newss2=surveyDefinitionPageDAO.store(ss2);
              SurveyDefinitionPage newss3=surveyDefinitionPageDAO.store(ss3);
              SurveyDefinitionPage newss4=surveyDefinitionPageDAO.store(ss4);
              int i=1;
              for (QuestionBank question:quest1)
              {
                  questionDAO.merge(new Question(question,newss0,i));
                  questionDAO.merge(new Question(question,newss1,i));
                  questionDAO.merge(new Question(question,newss2,i));
                  questionDAO.merge(new Question(question,newss3,i));
                  questionDAO.merge(new Question(question,newss4,i));
                  i++;
              }
  
  
              sall.add(ss0);
              sall.add(ss1);
              sall.add(ss2);
              sall.add(ss3);
              sall.add(ss4);
              dbSurveyDefinition.setPages(sall);
              return surveyDefinitionDAO.merge(dbSurveyDefinition);
              ///Glgnhof end added code
          }
  
          else{
              SurveyDefinition dbSurveyDefinition = surveyDefinitionDAO.findById(surveyDefinition.getId());
              dbSurveyDefinition.setName(surveyDefinition.getName());
              dbSurveyDefinition.setSurveyTheme(surveyDefinition.getSurveyTheme());
              dbSurveyDefinition.setDescription(surveyDefinition.getDescription());
              dbSurveyDefinition.setEmailInvitationTemplate(surveyDefinition.getEmailInvitationTemplate());
              dbSurveyDefinition.setCompletedSurveyTemplate(surveyDefinition.getCompletedSurveyTemplate());
              dbSurveyDefinition.setAutoRemindersDayOfMonth(surveyDefinition.getAutoRemindersDayOfMonth());
              dbSurveyDefinition.setAutoRemindersDays(surveyDefinition.getAutoRemindersDays());
              dbSurveyDefinition.setAutoRemindersFrequency(surveyDefinition.getAutoRemindersFrequency());
              dbSurveyDefinition.setAutoRemindersMonthlyOccurrence(surveyDefinition.getAutoRemindersMonthlyOccurrence());
              dbSurveyDefinition.setAutoRemindersWeeklyOccurrence(surveyDefinition.getAutoRemindersWeeklyOccurrence());
              dbSurveyDefinition.setCompletedSurveyTemplate(surveyDefinition.getCompletedSurveyTemplate());
              dbSurveyDefinition.setSendAutoReminders(surveyDefinition.getSendAutoReminders());
              dbSurveyDefinition.setAllowMultipleSubmissions(surveyDefinition.getAllowMultipleSubmissions());
              dbSurveyDefinition.setIsPublic(surveyDefinition.getIsPublic());
  
              dbSurveyDefinition.setMainCategory(surveyDefinition.getMainCategory());
  
  
  
  
              dbSurveyDefinition.setTechnology(surveyDefinition.getTechnology());
              dbSurveyDefinition.setLevel(surveyDefinition.getLevel());
              dbSurveyDefinition.setDifficultyLevel(surveyDefinition.getDifficultyLevel());
              dbSurveyDefinition.setNumericalDegree(surveyDefinition.getNumericalDegree());
              dbSurveyDefinition.setDepartment(departmentDAO.findById(surveyDefinition.getDepartment().getId()));
              SortedSet<SurveyDefinitionPage> sall= new TreeSet<SurveyDefinitionPage>();
  
              SurveyDefinitionPage ss0= new SurveyDefinitionPage();
              SurveyDefinitionPage ss1= new SurveyDefinitionPage();
              SurveyDefinitionPage ss2= new SurveyDefinitionPage();
              SurveyDefinitionPage ss3= new SurveyDefinitionPage();
              SurveyDefinitionPage ss4= new SurveyDefinitionPage();
  
              ss0.setSurveyDefinition(surveyDefinition);
              ss1.setSurveyDefinition(surveyDefinition);
              ss2.setSurveyDefinition(surveyDefinition);
              ss3.setSurveyDefinition(surveyDefinition);
              ss4.setSurveyDefinition(surveyDefinition);
  
              ss0.setTitle(surveyDefinition.getMainCategory().toString());
              ss1.setTitle(surveyDefinition.getTechnology().toString());
              ss2.setTitle(surveyDefinition.getLevel().toString());
              ss3.setTitle(surveyDefinition.getDifficultyLevel().toString());
              ss4.setTitle(surveyDefinition.getNumericalDegree().toString());
  
              ss0.setInstructions(surveyDefinition.getMainCategory().toString());
              ss1.setInstructions(surveyDefinition.getTechnology().toString());
              ss2.setInstructions(surveyDefinition.getLevel().toString());
              ss3.setInstructions(surveyDefinition.getDifficultyLevel().toString());
              ss4.setInstructions(surveyDefinition.getNumericalDegree().toString());
  
              ss0.setOrder((short)1);
              ss1.setOrder((short)2);
              ss2.setOrder((short)3);
              ss3.setOrder((short)4);
              ss4.setOrder((short)5);
  
              surveyDefinitionPageDAO.store(ss0);
              surveyDefinitionPageDAO.store(ss1);
              surveyDefinitionPageDAO.store(ss2);
              surveyDefinitionPageDAO.store(ss3);
              surveyDefinitionPageDAO.store(ss4);
  
              sall.add(ss0);
              sall.add(ss1);
              sall.add(ss2);
              sall.add(ss3);
              sall.add(ss4);
              dbSurveyDefinition.setPages(sall);
              */
          return surveyDefinitionDAO.merge(surveyDefinition);
      }
  
  
  
  
      @Transactional(readOnly = false)
      public SurveyDefinition surveyDefinition_update(SurveyDefinition surveyDefinition) {
  
          surveyDefinition.setStatus(SurveyDefinitionStatus.P);
          return surveyDefinitionDAO.merge(surveyDefinition);
      }
  
      @Transactional(readOnly = false)
      public SurveyDefinition surveyDefinition_updateLogo(Long id, byte[]  logo) {
          SurveyDefinition surveyDefinition =surveyDefinitionDAO.findById(id);
          surveyDefinition.setLogo(logo);
          return surveyDefinitionDAO.merge(surveyDefinition);
      }
  
      @Transactional(readOnly = false)
      public SurveyDefinition surveyDefinition_create(SurveyDefinition surveyDefinition, Long departmentId ) {
          surveyDefinition.setDepartment(departmentDAO.findById(departmentId));
  
          SortedSet<SurveyDefinitionPage> pages  = surveyDefinition.getPages();
          surveyDefinition =  surveyDefinitionDAO.merge(surveyDefinition);
          return  surveyDefinitionDAO.merge(surveyDefinition);
  
      }
      
      
  
  
  
      public Set<QuestionBankOption> questionOption_findAll()	throws DataAccessException {
          return questionOptionDAO.findAll();
      }
      public Set<QuestionBankOption> questionOption_findAll(int startResult,int maxRows) throws DataAccessException {
          return questionOptionDAO.findAll(startResult, maxRows);
      }
      public Long questionOption_getCount() {
          return questionOptionDAO.getCount();
      }
      public QuestionBankOption questionOption_findById(Long id) {
          return questionOptionDAO.findById(id);
      }
  
      public Set<QuestionBankOption> questionOption_findByQuestionId(QuestionBank id)	throws DataAccessException {
          return questionOptionDAO.findByQuestionId(id);
      }
      @Transactional(readOnly = false)
      public QuestionBankOption questionOption_merge(QuestionBankOption questionOption) {
          SortedSet<QuestionBankOption> updatedQuestionOptions = new TreeSet<QuestionBankOption>();
          Long questionId = questionOption.getQuestion().getId();
          QuestionBank question = questionDAO.findById(questionId);
          questionOption.setQuestion(question);
          Short order = question.updateSet(question.getOptions(), questionOption).getOrder();
          for (QuestionBankOption qo:  question.getOptions()) {
              if(qo.isRight())
              {
                  qo.setText(" "+qo.getText());
              }
              updatedQuestionOptions.add(questionOptionDAO.merge(qo));
          }
          question.setOptions(updatedQuestionOptions);
          question =questionDAO.merge(question);
          return question.getElement(question.getOptions(), order);
      }
      @Transactional(readOnly = false)
      public void questionOption_remove(QuestionOption questionOption) {
          QuestionBank question = questionDAO.findById(questionOption.getQuestion().getId());
          questionOptionDAO.remove(questionOption);
          question.removeGaps(question.getOptions());
      }
      @Transactional(readOnly = true)
      public void questionOption_removeQuestionOptionsByQuestionId(Long id) {
          questionOptionDAO.deleteByQuestionId(id);
      }
  
  
  
  
  
      public QuestionRowLabel questionRowLabel_findById(Long id) {
          return questionRowLabelDAO.findById(id);
      }
  
      public Set<QuestionRowLabel> questionRowLabel_findByQuestionId(Long id)	throws DataAccessException {
          return questionRowLabelDAO.findByQuestionId(id);
      }
      @Transactional(readOnly = false)
      public QuestionRowLabel questionRowLabel_merge(QuestionRowLabel questionRowLabel) {
          return questionRowLabelDAO.merge(questionRowLabel);
      }
      @Transactional(readOnly = false)
      public void questionRowLabel_remove(QuestionRowLabel questionRowLabel) {
          QuestionBank question = questionDAO.findById(questionRowLabel.getQuestion().getId());
          questionRowLabelDAO.remove(questionRowLabel);
          question.removeGaps(question.getOptions());
      }
  
  
  
  
      public QuestionColumnLabel questionColumnLabel_findById(Long id) {
          return questionColumnLabelDAO.findById(id);
      }
  
      public Set<QuestionColumnLabel> questionColumnLabel_findByQuestionId(Long id)	throws DataAccessException {
          return questionColumnLabelDAO.findByQuestionId(id);
      }
      @Transactional(readOnly = false)
      public QuestionColumnLabel questionColumnLabel_merge(QuestionColumnLabel questionColumnLabel) {
          return questionColumnLabelDAO.merge(questionColumnLabel);
      }
      @Transactional(readOnly = false)
      public void questionColumnLabel_remove(QuestionColumnLabel questionColumnLabel) {
          QuestionBank question = questionDAO.findById(questionColumnLabel.getQuestion().getId());
          questionColumnLabelDAO.remove(questionColumnLabel);
          question.removeGaps(question.getOptions());
      }
  
  
  
  
  
  
  
  
  
  
  
  
  
      public Set<QuestionBank> question_findAll()	throws DataAccessException {
          return questionDAO.findAll();
      }
      public Set<QuestionBank> question_findAll(int startResult,	int maxRows) throws DataAccessException {
          return questionDAO.findAll(startResult, maxRows);
      }
      public Set<QuestionBank> question_search(QuestionBank q,int startResult,	int maxRows) throws DataAccessException {
          return questionDAO.findBySearch( startResult, maxRows, q);
      }
      public Long question_getCount() {
          return questionDAO.getCount();
      }
      public QuestionBank question_findById(Long id) {
          return questionDAO.findById(id);
      }
      public QuestionBank question_findByOrder(Long surveyDefinitionId, Short pageOrder,Short questionOrder){
          return questionDAO.findByOrder(surveyDefinitionId,pageOrder,questionOrder);
      }
  
      @Transactional(readOnly = false)
      public QuestionBank question_merge(QuestionBank question) {
              return questionDAO.merge(question);
      }
  
      @Transactional(readOnly = false)
      public QuestionBank question_merge(QuestionBank question, SortedSet<QuestionBankOption> options) {
          //Clear data set field for non Dataset questions

              question =questionDAO.merge(question);

          if (!question.getType().getIsDataSet()) {
              question.setDataSetId(null);
          }
          //Deleting options from question that do not support options
          if (!question.getSuportsOptions()){
              questionOptionDAO.deleteByQuestionId(question.getId());
          }
          for (QuestionBankOption questionOption:  options) {
              questionOption.setQuestion(question);
              questionOption = questionOptionDAO.merge(questionOption);
          }
          question.setOptions(options);
          return question;
  
      }
  
  
  
  
  
  
  
  
  
      @Transactional(readOnly = false)
      public QuestionBank question_updateOptions(QuestionBank question) {
          Long questionId =question.getId();
          QuestionBank q = questionDAO.findById(questionId);
          questionOptionDAO.deleteByQuestionId(questionId);
          SortedSet<QuestionBankOption> options = new TreeSet<QuestionBankOption>();
          for (QuestionBankOption option: question.getOptionsList2()) {
  
              if (option.getValue()!= null && option.getText()!=null &&  			
                      option.getValue().trim().length() > 0 && option.getText().trim().length() > 0 ) {
                  QuestionBankOption questionOption  = new QuestionBankOption(q,option.getOrder(),option.getValue(),option.getText(),option.isRight());
                  questionOption = questionOptionDAO.merge(questionOption);
                  options.add(questionOption);
              }
          }
          q.setOptions(options);
          return questionDAO.merge(q);
      }
  
  

  

  
  
      @Transactional(readOnly = false)
      public void question_remove(Long questionId){
          QuestionBank question = questionDAO.findById(questionId);

          log.info("deleting  questionOptions");
          questionOptionDAO.deleteByQuestionId(question.getId());
          question.setOptions(null);
          questionDAO.remove(question);
      }
  
  
  
  
  
      public Set<DataSet> dataSet_findAll()	throws DataAccessException {
          return dataSetDAO.findAll();
      }
      public Set<DataSet> dataSet_findAll(int startResult,	int maxRows) throws DataAccessException {
          return dataSetDAO.findAll(startResult, maxRows);
      }
      public Long dataSet_getCount() {
          return dataSetDAO.getCount();
      }
      public DataSet dataSet_findById(Long id) {
          return dataSetDAO.findById(id);
      }
  
      /*
      public DataSet dataset_findByCode(String code) {
          return dataSetDAO.findByCode(code);
          }
       */
      public DataSet dataset_findByName(String name) {
          return dataSetDAO.findByName(name);	
      }
  
      @Transactional(readOnly = false)
      public DataSet dataSet_merge(DataSet dataSet) {
          return dataSetDAO.merge(dataSet);
      }
      @Transactional(readOnly = false)
      public void dataSet_remove(Long id) {
          dataSetItemDAO.deleteByDataSetId(id);
          DataSet dataSet = dataSetDAO.findById(id);
          dataSet.setItems(null);
          dataSetDAO.remove(dataSet);
      }
  
  
  
  
      public Set<DataSetItem> datasetItem_findAll()	throws DataAccessException {
          return dataSetItemDAO.findAll();
      }
      public Set<DataSetItem> datasetItem_findAll(int startResult,int maxRows) throws DataAccessException {
          return dataSetItemDAO.findAll(startResult, maxRows);
      }
      public Long datasetItem_getCount(Long id) {
          return dataSetItemDAO.getCount(id);
      }
      public DataSetItem datasetItem_findById(Long id) {
          return dataSetItemDAO.findById(id);
      }
  
      public Set<DataSetItem> datasetItem_findByDataSetId(Long id)	throws DataAccessException {
          return dataSetItemDAO.findByDataSetId(id);
      }
      public Set<DataSetItem> datasetItem_findByDataSetId(Long id,int startResult, int maxRows)	throws DataAccessException {
          return dataSetItemDAO.findByDataSetId(id,startResult,maxRows);
      }
      @Transactional(readOnly = false)
      public DataSetItem datasetItem_merge(DataSetItem dataSetItem) {
          return dataSetItemDAO.merge(dataSetItem);
      }
      @Transactional(readOnly = false)
      public void datasetItem_remove(DataSetItem dataSetItem) {
          dataSetItemDAO.remove(dataSetItem);
      }
      @Transactional(readOnly = false)
      public void datasetItem_deleteByDataSetId(Long datasetId) {
          dataSetItemDAO.deleteByDataSetId(datasetId);
      }
  
      @Transactional(readOnly = false)
      public void importDatasetItems (CSVReader csvReader, Long datasetId,Boolean ignoreFirstRow) {
          try {
              DataSet	 dataSet = dataSetDAO.findById(datasetId);
              dataSetItemDAO.deleteByDataSetId(datasetId);				
  
              Integer order = 1;
              short valueFieldIndex = 0;
              short textFieldIndex = 1;
              Boolean autoGenerateValues = false;
              DataSetItem dataSetItem;
  
              String [] nextLine;
              while ((nextLine = csvReader.readNext()) != null) {
                  //skip first row
                  if (ignoreFirstRow) {
                      //Will skip the first row the continue on with loop
                      ignoreFirstRow=false;
                      continue;}
                  if (order == 1) { // check the first Row
                      if (nextLine.length == 1)  {
                          //only one column
                          autoGenerateValues = true;
                          textFieldIndex = 0;
                      }
                      else {
                          //more than one column use the first two 
                          autoGenerateValues = false;
                          if (nextLine[0].trim().length() >  nextLine[1].trim().length()) {
                              //invert the indexes
                              valueFieldIndex = 1;
                              textFieldIndex = 0;
                          } 
                      }
                  }
                  if (autoGenerateValues) {
                      dataSetItem = new DataSetItem(dataSet,order,order.toString(),nextLine[textFieldIndex].trim());
                  }
                  else{
                      dataSetItem = new DataSetItem(dataSet,order,nextLine[valueFieldIndex].trim(),nextLine[textFieldIndex].trim());
                  }
                  dataSetItemDAO.merge(dataSetItem);
                  order++;
              }
          }
          catch (Exception e) {
              log.error(e.getMessage(), e);
              throw new RuntimeException(e);
          }
      }
  
  
  
      public String exportDatasetItemsToCommaDelimited (Long datasetId) {
          try {
              DataSetItem dataSetItem;
              StringBuilder stringBuilder  = new StringBuilder();
              stringBuilder.append("value,text\n");
              Set<DataSetItem> dataSetItems = dataSetItemDAO.findByDataSetId(datasetId);
              Iterator<DataSetItem> it;
              it = dataSetItems.iterator();
              while (it.hasNext()) {
                  dataSetItem= it.next();
                  stringBuilder.append(dataSetItem.getValue());					
                  stringBuilder.append(",");
                  stringBuilder.append(dataSetItem.getText());
                  stringBuilder.append("\n");
              }
              return stringBuilder.toString();
          }
          catch (Exception e) {
              log.error(e.getMessage(), e);
              throw new RuntimeException(e);
          }
      }
  
  
  
  
  
  
  
  
      public SortedSet<Invitation> invitation_findSurveyAll(Long surveyDefinitionId) throws DataAccessException {
          return invitationDAO.findSurveyAll(surveyDefinitionId);
      }
      public SortedSet<Invitation> invitation_findSurveyAll(Long surveyDefinitionId,int startResult,int maxRows) throws DataAccessException {
          return invitationDAO.findSurveyAll(surveyDefinitionId , startResult, maxRows);
      }
      public Long invitation_getSurveyCount(Long surveyDefinitionId) {
          return invitationDAO.getSurveyCount(surveyDefinitionId);
      }
      public Long invitation_getSurveyOpenedCount(Long surveyDefinitionId) {
          return invitationDAO.getSurveyOpenedCount(surveyDefinitionId);
      }
      public Invitation invitation_findById(Long id) {
          return invitationDAO.findById(id);
      }
      public Invitation invitation_findByUuid(String uuid) {
          return invitationDAO.findByUuid(uuid);
      }
      public SortedSet<Invitation> invitation_searchByFirstName(String firstName)  {
          return invitationDAO.searchByFirstName(firstName);
      }
      public SortedSet<Invitation> invitation_searchByLastName(String lastName){
          return invitationDAO.searchByLastName(lastName);
      }
      public SortedSet<Invitation> invitation_searchByFirstNameAndLastName(String firstName , String lastName){
          return invitationDAO.searchByFirstNameAndLastName(firstName,lastName);
      }
      public SortedSet<Invitation> invitation_searchByEmail(String email){
          return invitationDAO.searchByEmail(email);
      }
  
  
      @Transactional(readOnly = false)
      public void invitation_send(Invitation invitation,
              String emailSubject, 
              String emailContent
              ) {
          try {
              //Save the invitation entry
              invitation = invitationDAO.merge(invitation);
              //Send Invitation Email
              mailService.sendEmail(invitation.getEmail(), emailSubject ,emailContent);
          }
          catch (Exception e) {
              log.error(e.getMessage(), e);
              throw new RuntimeException(e);
          }
      }
  
  
  
  
      @Transactional(readOnly = false)
      public void invitation_updateAsRead(Long id) {
          Invitation invitation  = invitationDAO.findById(id);
          invitation.updateAsRead();
          invitationDAO.merge(invitation);
      }
  
  
      public Set<Tags> tags_findAll()	throws DataAccessException {
          return tagsDAO.findAll();
      }

      public Set<Tags> tags_findByDepartments(User user)	throws DataAccessException {
          return tagsDAO.findByDepartments(user.getDepartments());
      }
  
  
      public Tags tags_findById(Long id)	throws DataAccessException {
          return tagsDAO.findById(id);
      }
  
  
  
      public Set<RegularExpression> regularExpression_findAll()	throws DataAccessException {
          return regularExpressionDAO.findAll();
      }
      public Set<RegularExpression> regularExpression_findAll(int startResult,	int maxRows) throws DataAccessException {
          return regularExpressionDAO.findAll(startResult, maxRows);
      }
      public Long regularExpression_getCount(Long id) {
          return regularExpressionDAO.getCount(id);
      }
  
      public Long regularExpression_getCount() {
          return regularExpressionDAO.getCount();
      }
  
      public RegularExpression regularExpression_findById(Long id) {
          return regularExpressionDAO.findById(id);
      }
      public RegularExpression regularExpression_findByName(String name) {
          return regularExpressionDAO.findByName(name);
      }
      @Transactional(readOnly = false)
      public RegularExpression regularExpression_merge(RegularExpression regularExpression) {
          return regularExpressionDAO.merge(regularExpression);
      }
      @Transactional(readOnly = false)
      public void regularExpression_remove(RegularExpression regularExpression) {
          regularExpressionDAO.remove(regularExpression);
      }
  
  
  
  
      public Set<Sector> sector_findAll()	throws DataAccessException {
          return sectorDAO.findAll();
      }
      public Set<Sector> sector_findAll(int startResult,	int maxRows) throws DataAccessException {
          return sectorDAO.findAll(startResult, maxRows);
      }
      public Long sector_getCount() {
          return sectorDAO.getCount();
      }
      public Sector sector_findById(Long id) {
          return sectorDAO.findById(id);
      }
      public Sector sector_findByName(String name) {
          return sectorDAO.findByName(name);	
      }
      @Transactional(readOnly = false)
      public Sector sector_merge(Sector sector) {
          return sectorDAO.merge(sector);
      }
      @Transactional(readOnly = false)
      public void sector_remove(Long id) {
          surveyTemplateDAO.deleteBySectorId(id);
          Sector sector = sectorDAO.findById(id);
          sector.setTemplates(null);
          sectorDAO.remove(sector);
      }
  
  
  
      public Set<SurveyTemplate> surveyTemplate_findAll()	throws DataAccessException {
          return surveyTemplateDAO.findAll();
      }
      public Set<SurveyTemplate> surveyTemplate_findAll(int startResult,int maxRows) throws DataAccessException {
          return surveyTemplateDAO.findAll(startResult, maxRows);
      }
      public Long surveyTemplate_getCount(Long id) {
          return surveyTemplateDAO.getCount(id);
      }
      public SurveyTemplate surveyTemplate_findById(Long id) {
          return surveyTemplateDAO.findById(id);
      }
      public Set<SurveyTemplate> surveyTemplate_findBySectorId(Long id)	throws DataAccessException {
          return surveyTemplateDAO.findBySectorId(id);
      }
      public Set<SurveyTemplate> surveyTemplate_findBySectorId(Long id,int startResult, int maxRows)	throws DataAccessException {
          return surveyTemplateDAO.findBySectorId(id,startResult,maxRows);
      }
      @Transactional(readOnly = false)
      public SurveyTemplate surveyTemplate_merge(SurveyTemplate surveyTemplate) {
          return surveyTemplateDAO.merge(surveyTemplate);
      }
      @Transactional(readOnly = false)
      public void surveyTemplate_remove(SurveyTemplate surveyTemplate) {
          surveyTemplateDAO.remove(surveyTemplate);
      }
      @Transactional(readOnly = false)
      public void surveyTemplate_deleteBySectorId(Long sectorId) {
          surveyTemplateDAO.deleteBySectorId(sectorId);
      }
  
      public boolean surveyTemplate_ValidateNameIsUnique(SurveyTemplate surveyTemplate) {
  
          Boolean isValid = true;
          //get all survey definitions with the same name
          Set<SurveyTemplate> surveyTemplates = surveyTemplateDAO.findByName(surveyTemplate.getName());
  
  
          //Insert case  null id
          if (surveyTemplate.getId() == null) {
  
              // check that there are not survey types with the same name under the current department
              for (SurveyTemplate appType : surveyTemplates) {
                  if (appType.getSector().getId().equals(surveyTemplate.getSector().getId())) {
                      isValid= false;
                      break;
                  }
              }
          }
          //Update case id not  null
          else{
              // check that there are no other survey types with the same name under the current department
              for (SurveyTemplate appType : surveyTemplates) {
                  if (appType.getSector().getId().equals(surveyTemplate.getSector().getId()) &&
                          !appType.getId().equals(surveyTemplate.getId())) {
                      isValid= false;
                      break;
                  }
              }
          }
          return isValid;
      }
  
      @Transactional(readOnly = false)
      public Set<Day> day_findAll() throws DataAccessException {
          return dayDAO.findAll();
      }
  
      @Transactional(readOnly = false)
      public Day day_findById(Long id) throws DataAccessException {
          return dayDAO.findById(id);
  
      }



      @Transactional(readOnly = false)
      public Tags questionBankAddTag(String tagName) {
          Tags addTag=new Tags();
          addTag.setTagName(tagName);
          Tags chkTag=tagsDAO.findByTagName(tagName);
          if(chkTag!=null) {
              return tagsDAO.findByTagName(tagName);
          }
          else
              return tagsDAO.merge(addTag);
      }




















  }
