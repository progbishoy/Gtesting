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
import com.jd.survey.dto.QuestionHolder;
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
@Service("SurveySettingsService")
public class SurveySettingsService {

	private static final Log log = LogFactory.getLog(SurveySettingsService.class);
	private static final String INVITATION_EMAIL_TITLE = "invitation_email_title";
	private static final String INVITEE_FULLNAME_PARAMETER_NAME = "invitee_fullname_parameter_name";
	private static final String SURVEY_NAME = "survey_name";
	private static final String INVITE_FILL_SURVEY_LINK_PARAMETER_NAME = "invite_fill_survey_link_parameter_name";
	private static final String INVITE_FILL_SURVEY_LINK_LABEL = "invite_fill_survey_link_label";
	private static final String INTERNAL_SITE_BASE_URL = "internal_site_base_url";
	private static final String EXTERNAL_SITE_BASE_URL = "external_site_base_url";
	@Autowired	private MessageSource messageSource;
	@Autowired	private DepartmentDAO departmentDAO;
	@Autowired	private SurveyDefinitionDAO surveyDefinitionDAO;
	@Autowired	private SurveyDefinitionPageDAO surveyDefinitionPageDAO;
	@Autowired	private QuestionOptionDAO questionOptionDAO;
	@Autowired	private QuestionRowLabelDAO rowLabelDAO;
	@Autowired	private QuestionColumnLabelDAO columnLabelDAO;
	@Autowired	private QuestionRowLabelDAO questionRowLabelDAO;
	@Autowired	private QuestionColumnLabelDAO questionColumnLabelDAO;
	@Autowired	private QuestionDAO questionDAO;
	@Autowired
	private QuestionBankDAO questionBankDAO;
	@Autowired	private DataSetDAO dataSetDAO;
	@Autowired	private DataSetItemDAO dataSetItemDAO;
	@Autowired	private VelocityTemplateDAO velocityTemplateDAO;
	@Autowired  private RegularExpressionDAO regularExpressionDAO;
	@Autowired
	private SurveyDAO surveyDAO;
	@Autowired private InvitationDAO invitationDAO;
	@Autowired private MailService mailService;
	@Autowired private SectorDAO sectorDAO;
	@Autowired private SurveyTemplateDAO surveyTemplateDAO;
	@Autowired private DayDAO dayDAO;
	@Autowired private ReportDAO reportDAO;
	@Autowired
	private TagsDAO tagsDAO;
	@Autowired
	private SurveyTagsDAO surveyTagsDAO;
	@Autowired
	private QuestionBankOptionDAO questionBankOptionDAO;

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


	public boolean question_ValidateMinMaxDoubleValues(Question question) {
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
	
	public boolean question_ValidateMinMaxValues(Question question) {
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




	public boolean question_ValidateDateRange (Question question){
		return !(question.getDateMinimum() != null &&
				question.getDateMaximum() != null &&
				question.getDateMinimum().after(question.getDateMaximum()));
	}

	public boolean question_ValidateMinMaxDoubleValues(QuestionBank question) {
		if (question.getType() == QuestionType.CURRENCY_INPUT ||
				question.getType() == QuestionType.DECIMAL_INPUT) {
			if (question.getDecimalMinimum() != null &&
					question.getDecimalMaximum() != null &&
					question.getDecimalMinimum().doubleValue() >= question.getDecimalMaximum().doubleValue()) {
				return false;
			}
		}
		return true;
	}

	public boolean question_ValidateMinMaxValues(QuestionBank question) {
		if (question.getType() == QuestionType.INTEGER_INPUT ||
				question.getType() == QuestionType.SHORT_TEXT_INPUT ||
				question.getType() == QuestionType.LONG_TEXT_INPUT ||
				question.getType() == QuestionType.HUGE_TEXT_INPUT) {
			if (question.getIntegerMinimum() != null &&
					question.getIntegerMaximum() != null &&
					question.getIntegerMinimum() >= question.getIntegerMaximum()) {
				return false;
			}
		}
		return true;
	}


	public boolean question_ValidateDateRange(QuestionBank question) {
		return !(question.getDateMinimum() != null &&
				question.getDateMaximum() != null &&
				question.getDateMinimum().after(question.getDateMaximum()));
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

			for (SurveyTags surveyTag : surveyDefinition.getSurveyTags()) {


				Tags Tag = surveyTag.getTag();
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
			return surveyDefinitionDAO.merge(dbSurveyDefinition);


		}else {
			surveyDefinition.setDepartment(departmentDAO.findById(surveyDefinition.getDepartment().getId()));
		}
		return surveyDefinitionDAO.merge(surveyDefinition);

	}
	public Set<SurveyTags> getTags(SurveyDefinition surveyDefinition) {
		return surveyTagsDAO.findBySurveyId(surveyDefinition);
	}
	@Transactional(readOnly = false)
	public SurveyDefinition generateQuestionsFromTags(SurveyDefinition surveyDefinition) {
		//Glgnh0fGen
		Set<QuestionBank> selectedQuestionsPerTag = new TreeSet<QuestionBank>();
		Short porder = 1;
		 int durationTotal=0;
		for (SurveyTags tag : surveyTagsDAO.findBySurveyId(surveyDefinition)) {
			if (!(tag.getEasy() == 0)) {
				selectedQuestionsPerTag.addAll(findByTagAndDifficultyAndLimit(tag.getTag(), QuestionDifficultyLevel.EASY, tag.getEasy()));
			}
			if (!(tag.getMedium() == 0)) {
				selectedQuestionsPerTag.addAll(findByTagAndDifficultyAndLimit(tag.getTag(), QuestionDifficultyLevel.MEDIUM, tag.getMedium()));
			}
			if (!(tag.getHard() == 0)) {
				selectedQuestionsPerTag.addAll(findByTagAndDifficultyAndLimit(tag.getTag(), QuestionDifficultyLevel.HIGH, tag.getHard()));
			}

			// count time BISHOY and insert it oki
			SurveyDefinitionPage page = new SurveyDefinitionPage();
			page.setSurveyDefinition(surveyDefinition);
			page.setTitle(tag.getTag().getTagName());
			page.setOrder(porder);
			page.setInstructions("");
			page = surveyDefinitionPageDAO.merge(page);
           
			Short qorder = 1;
			for (QuestionBank qb : selectedQuestionsPerTag) {
				int questionduration=qb.getDuration()==null?5:qb.getDuration().intValue();
				durationTotal=durationTotal+	questionduration;
				Question q = new Question(qb);
				q.setOrder(new Short(qorder));
				q.setPage(page);
				q = questionDAO.merge(q);
				Set<QuestionBankOption> questionOptionsList = questionBankOption_findByQuestionId(qb);
				Short qoorder = 1;
				for (QuestionBankOption qol : questionOptionsList) {
					QuestionOption qo = new QuestionOption();
					qo.setQuestion(q);
					qo.setOrder(qoorder);
					qo.setValue(qol.getValue());
					qo.setText(qol.getText());
					qo.setRight(qol.isRight());
					questionOptionDAO.merge(qo);
					qoorder = (short) (qoorder + 1);
				}
				qorder = (short) (qorder + 1);
			}
			porder = (short) (porder + 1);
			selectedQuestionsPerTag.clear();
		}
// insert totalduration and surveyId link or alert table surveydefinition and insert grandtotal time ;
                  surveyDefinition.setExamtime(new Integer(durationTotal));
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

		for (SurveyDefinitionPage page : pages){
			page.setSurveyDefinition(surveyDefinition);
			SortedSet<Question> questions =  page.getQuestions();
			page = surveyDefinitionPageDAO.merge(page);
			for (Question question: questions){
				question.setPage(page);
				SortedSet<QuestionOption> options  = question.getOptions();
				SortedSet<QuestionRowLabel> rowLabels  = question.getRowLabels();
				SortedSet<QuestionColumnLabel> columnLabels  = question.getColumnLabels();
				question = questionDAO.merge(question);
				for (QuestionOption option: options){
					option.setQuestion(question);
					option= questionOptionDAO.merge(option);
				}
				question.setOptions(options);
				for (QuestionRowLabel rowLabel: rowLabels){
					rowLabel.setQuestion(question);
					rowLabel= rowLabelDAO.merge(rowLabel);
				}
				question.setRowLabels(rowLabels);
				for (QuestionColumnLabel columnLabel: columnLabels){
					columnLabel.setQuestion(question);
					columnLabel= columnLabelDAO.merge(columnLabel);
				}
				question.setColumnLabels(columnLabels);
			}
			page.setQuestions(questions);
		}
		surveyDefinition.setPages(pages);
		return  surveyDefinitionDAO.merge(surveyDefinition);

	}


	@Transactional(readOnly = false)
	public void surveyDefinition_remove(SurveyDefinition surveyDefinition) {

		for (SurveyDefinitionPage page : surveyDefinition.getPages()){
			for (Question question: page.getQuestions()){
				log.info("deleting  questionOptions");	
				questionOptionDAO.deleteByQuestionId(question.getId());
				rowLabelDAO.deleteByQuestionId(question.getId());
				columnLabelDAO.deleteByQuestionId(question.getId());	
			}
			log.info("deleting  questions");
			questionDAO.deleteBySurveyDefinitionPageId(page.getId());
		}
		Set<SurveyTags> tags=  surveyTagsDAO.findBySurveyId(surveyDefinition);
		for (SurveyTags tag : tags) {
			log.info("deleting  questions");
			surveyTagsDAO.remove(tag);
		}
	
		log.info("deleting  SurveyDefinitionPages");
		surveyDefinitionPageDAO.deleteBySurveyDefinitionId(surveyDefinition.getId());

		surveyDefinition.setPages(null);
		surveyDefinition.setSurveyTags(null);
		surveyDefinitionDAO.remove(surveyDefinition);

	}


	public Set<SurveyDefinition> surveyDefinition_findByName(String name) throws DataAccessException {
		return surveyDefinitionDAO.findByName(name);
	}


	public Boolean surveyDefinition_ValidateNameIsUnique(SurveyDefinition surveyDefinition) throws DataAccessException {
		Boolean isValid = true;
		//get all survey definitions with the same name
		Set<SurveyDefinition> surveyDefinitions = surveyDefinitionDAO.findByName(surveyDefinition.getName());

		//Insert case  null id
		if (surveyDefinition.getId() == null) {
			// check that there are not survey types with the same name under the current department
			for (SurveyDefinition appType : surveyDefinitions) {
				if (appType.getDepartment().getId().equals(surveyDefinition.getDepartment().getId())) {
					isValid= false;
					break;
				}
			}
		}
		//Update case id not  null
		else{
			// check that there are no other survey types with the same name under the current department
			for (SurveyDefinition appType : surveyDefinitions) {
				if (appType.getDepartment().getId().equals(surveyDefinition.getDepartment().getId()) &&
						!appType.getId().equals(surveyDefinition.getId())) {
					isValid= false;
					break;
				}
			}
		}
		return isValid;
	}


	/**
	 * Validates the Survey Questions. 
	 * It will return false if there is an empty page or a survey question without options 
	 *    
	 */
	public Boolean surveyDefinition_ValidateSurveydefinitionForPublishing (Long id){
		SurveyDefinition surveyDefinition =  surveyDefinitionDAO.findById(id);
		if (surveyDefinition.getPages().isEmpty() ){
			return false; //no pages
		}
		for (SurveyDefinitionPage page : surveyDefinition.getPages()){ 
			if(page.getQuestions().isEmpty()){
				return false; //empty page
			} 
			for (Question question : page.getQuestions()){
				//Questions with Options
				if (question.getType().getRequiresOptions()) {
					if(question.getOptions().isEmpty()){
						return false; //Multiple choice question with no options 
					}
				}
				//Matrix Questions
				if (question.getType().getIsMatrix()) {
					if(question.getColumnLabels().isEmpty()){
						return false; //Matrix  question with no column labels 
					}
					if(question.getRowLabels().isEmpty()){
						return false; //Matrix  question with no row labels 
					}
				}
			}
		}
		return true;
	} 


	@Transactional(readOnly = false)
	public SurveyDefinition surveyDefinition_publish(Long id){
		SurveyDefinition surveyDefinition =  surveyDefinitionDAO.findById(id);
		if (surveyDefinition.getStatus().equals(SurveyDefinitionStatus.I) ) {  
			surveyDAO.publish(surveyDefinition);
		}
		surveyDefinition.setStatus( SurveyDefinitionStatus.P);
		return surveyDefinitionDAO.merge(surveyDefinition);
	} 


	@Transactional(readOnly = false)
	public SurveyDefinition surveyDefinition_deactivate(Long id){
		SurveyDefinition surveyDefinition = surveyDefinitionDAO.findById(id);
		surveyDefinition.setStatus( SurveyDefinitionStatus.D);
		return surveyDefinitionDAO.merge(surveyDefinition);
	} 

	public Set<SurveyDefinition> surveyDefinition_findByUserLogin( User user) throws DataAccessException {
		return surveyDefinitionDAO.getSurveyDefinitionUsers(user.getLogin(), -1, -1);
	}

	public Set<SurveyDefinition> surveydefinition_findAll( User user) throws DataAccessException {


		return surveyDefinitionDAO.getSurveyDefinitionUsers(user.getLogin(), -1, -1);

	}


	@Transactional()
	@SuppressWarnings("unchecked")
	public void sendEmailReminder(SurveyDefinition surveyDefinition) {
		try {
			
				StringWriter sw = new StringWriter();
				Map<String, String> model = new HashMap<String, String>();
			
			 for(User user : surveyDefinition.getUsers()){
				String emailSubject = messageSource.getMessage(INVITATION_EMAIL_TITLE, null, LocaleContextHolder.getLocale());
				String surveyLink =messageSource.getMessage(EXTERNAL_SITE_BASE_URL, null, LocaleContextHolder.getLocale());
				String trackingImageLink =messageSource.getMessage(INTERNAL_SITE_BASE_URL, null, LocaleContextHolder.getLocale());

				if (trackingImageLink.endsWith("/")) {trackingImageLink = trackingImageLink +"public/w/";}	else {trackingImageLink = trackingImageLink +"/public/w/";}
				if (surveyLink.endsWith("/")) {surveyLink = surveyLink +"private/";}	else {surveyLink = surveyLink +"/private/";}	

				String emailContent="";	
				Invitation invitation = new Invitation(user.getFirstName().trim(),
						user.getMiddleName().trim(),
						user.getLastName().trim(),
						user.getEmail().trim(),
						surveyDefinition);
				
				//survey name
				model.put(messageSource.getMessage(SURVEY_NAME, null, LocaleContextHolder.getLocale()).replace("${", "").replace("}", ""), 
						surveyDefinition.getName());
				//full name
				model.put(messageSource.getMessage(INVITEE_FULLNAME_PARAMETER_NAME, null, LocaleContextHolder.getLocale()).replace("${", "").replace("}", ""), 
						user.getFullName());
				//survey link
				model.put(messageSource.getMessage(INVITE_FILL_SURVEY_LINK_PARAMETER_NAME, null, LocaleContextHolder.getLocale()).replace("${", "").replace("}", ""), 
						"<a href='" + surveyLink + surveyDefinition.getId() + "?list'>" + messageSource.getMessage(INVITE_FILL_SURVEY_LINK_LABEL, null, LocaleContextHolder.getLocale())  +"</a>");
				
				VelocityContext velocityContext = new VelocityContext(model);
				Velocity.evaluate(velocityContext, sw, "velocity-log" , 
						surveyDefinition.getEmailInvitationTemplate());
				emailContent =  sw.toString().trim();


				if (emailContent.length() > 14
						&& emailContent.substring(emailContent.length()-14).toUpperCase().equalsIgnoreCase("</BODY></HTML>")) {
					emailContent = emailContent.substring(0,emailContent.length()-14)  +"<img src='" + trackingImageLink + invitation.getUuid() + "'/></BODY></HTML>";
				}
				else{
					// template is incorrect or not html do not include tracking white gif
					//emailContent = emailContent + "<img src='" +  trackingImageLink + invitation.getUuid() + "'/></BODY></HTML>";
				}
				surveyDefinition.setAutoReminderLastSentDate(new Date());
				mailService.sendEmail(user.getEmail(), emailSubject ,emailContent);
				
			}
		}	
		catch (RuntimeException e) {
			log.error(e.getMessage(),e);
			System.out.println(e);
			throw(new RuntimeException(e));
		} 
	}

	@Transactional(readOnly = false)
	@SuppressWarnings("unchecked")
	public void sendEmailReminders() {
		try {
			int currentDayOfWeek = new DateTime().getDayOfWeek();
			int currentDayOfMonth = new DateTime().getDayOfMonth();
			DateTime todayDateTime = new DateTime();

			for (SurveyDefinition surveyDefinition : surveyDefinitionDAO.findAllInternal()){
				if (surveyDefinition.getSendAutoReminders()&& surveyDefinition.getUsers().size()>0 && surveyDefinition.getStatusAsString().equals("P")) {
					Date  lastSentDate = surveyDefinition.getAutoReminderLastSentDate(); 
					switch (surveyDefinition.getAutoRemindersFrequency()) {
					case  WEEKLY:
						int weeks;
						if (lastSentDate !=null) {weeks = Weeks.weeksBetween(new DateTime(lastSentDate),todayDateTime).getWeeks();
						}
						
						else {weeks = 1000;}
						if (weeks >= surveyDefinition.getAutoRemindersWeeklyOccurrence()) {
							for (Day day : surveyDefinition.getAutoRemindersDays()) {
								if (day.getId().equals(new Long(currentDayOfWeek))) {
									sendEmailReminder(surveyDefinition);
									
								}
							}
						}
						break;
					case MONTHLY:
						int months;
						if (lastSentDate !=null) {months = Months.monthsBetween(new DateTime(lastSentDate), todayDateTime).getMonths();
						}
						else {months = 1000;}
						if (months>=surveyDefinition.getAutoRemindersMonthlyOccurrence() && surveyDefinition.getAutoRemindersDayOfMonth().equals(currentDayOfMonth)) {
							sendEmailReminder(surveyDefinition);
							}
						break;
					}
				}
			}
		}
		catch (RuntimeException e) {
			log.error(e.getMessage(),e);
		} 
	}

	

	public Set<SurveyDefinitionPage> surveyDefinitionPage_findAll()	throws DataAccessException {
		return surveyDefinitionPageDAO.findAll();
	}
	public Set<SurveyDefinitionPage> surveyDefinitionPage_findAll(int startResult,int maxRows) throws DataAccessException {
		return surveyDefinitionPageDAO.findAll(startResult, maxRows);
	}
	public Long surveyDefinitionPage_getCount() {
		return surveyDefinitionPageDAO.getCount();
	}
	public SurveyDefinitionPage surveyDefinitionPage_findById(Long id) {
		return surveyDefinitionPageDAO.findById(id);
	}
	@Transactional(readOnly = false)
	public SurveyDefinitionPage surveyDefinitionPage_merge(SurveyDefinitionPage surveyDefinitionPage) {
		SortedSet<SurveyDefinitionPage> updatedSurveyDefinitionPages = new TreeSet<SurveyDefinitionPage>();
		Long AppTypeId = surveyDefinitionPage.getSurveyDefinition().getId();
		SurveyDefinition surveyDefinition = surveyDefinitionDAO.findById(AppTypeId);
		surveyDefinitionPage.setSurveyDefinition(surveyDefinition);
		Short order = surveyDefinition.updateSet(surveyDefinition.getPages(), surveyDefinitionPage).getOrder();
		for (SurveyDefinitionPage atp:  surveyDefinition.getPages()) {
			updatedSurveyDefinitionPages.add(surveyDefinitionPageDAO.merge(atp));
		}
		surveyDefinition.setPages(updatedSurveyDefinitionPages);
		surveyDefinition=surveyDefinitionDAO.merge(surveyDefinition);
		return surveyDefinition.getElement(surveyDefinition.getPages(), order);
	}

	@Transactional(readOnly = false)
	public void surveyDefinitionPage_updateSkipAndBranckLogic(SurveyDefinitionPage surveyDefinitionPage) {
		SurveyDefinitionPage dbsurveyDefinitionPage = surveyDefinitionPageDAO.findById(surveyDefinitionPage.getId());
		dbsurveyDefinitionPage.setPageLogic(surveyDefinitionPage.getPageLogic());
		dbsurveyDefinitionPage.updateJson();
		surveyDefinitionPageDAO.merge(dbsurveyDefinitionPage);

	}





	@Transactional(readOnly = false)
	public void surveyDefinitionPage_remove(SurveyDefinitionPage surveyDefinitionPage) {
		SurveyDefinition surveyDefinition = surveyDefinitionDAO.findById(surveyDefinitionPage.getSurveyDefinition().getId());
		for (Question question: surveyDefinitionPage.getQuestions()){
			log.info("deleting  questionOptions");	
			questionOptionDAO.deleteByQuestionId(question.getId());
			rowLabelDAO.deleteByQuestionId(question.getId());
			columnLabelDAO.deleteByQuestionId(question.getId());
		}
		log.info("deleting  questions");
		questionDAO.deleteBySurveyDefinitionPageId(surveyDefinitionPage.getId());
		surveyDefinitionPage.setQuestions(null);
		surveyDefinitionPageDAO.remove(surveyDefinitionPage);
		surveyDefinition.removeGaps(surveyDefinition.getPages());
	}




	public Boolean surveyDefinitionPage_ValidateTitleIsUnique(SurveyDefinitionPage surveyDefinitionPage) throws DataAccessException {
		Boolean isValid = true;
		//get all appplication types with the same name
		Set<SurveyDefinitionPage> surveyDefinitionPages = surveyDefinitionPageDAO.findByTitle(surveyDefinitionPage.getTitle());

		//Insert case  null id
		if (surveyDefinitionPage.getId() == null) {
			// check that there are not survey types with the same name under the current Department
			for (SurveyDefinitionPage atp : surveyDefinitionPages) {
				if (atp.getSurveyDefinition().getId().equals(surveyDefinitionPage.getSurveyDefinition().getId())) {
					isValid= false;
					break;
				}
			}
		}
		//Update case id not  null
		else{
			// check that there are no other survey types with the same name under the current license
			for (SurveyDefinitionPage atp : surveyDefinitionPages){
				if (atp.getSurveyDefinition().getId().equals(surveyDefinitionPage.getSurveyDefinition().getId()) &&

						!atp.getId().equals(surveyDefinitionPage.getId())){

					isValid= false;
					break;
				}
			}
		}
		return isValid;
	}



	public Set<VelocityTemplate> velocityTemplate_findAll()	throws DataAccessException {
		return velocityTemplateDAO.findAll();
	}
	public Set<VelocityTemplate> velocityTemplate_findAll(int startResult,int maxRows) throws DataAccessException {
		return velocityTemplateDAO.findAll(startResult, maxRows);
	}
	public Long velocityTemplate_getCount() {
		return velocityTemplateDAO.getCount();
	}
	public VelocityTemplate velocityTemplate_findById(Long id) {
		return velocityTemplateDAO.findById(id);
	}
	public VelocityTemplate velocityTemplate_findByName(String templateName) {
		return velocityTemplateDAO.findByName(templateName);
	}
	@Transactional(readOnly = false)
	public VelocityTemplate velocityTemplate_merge(VelocityTemplate velocityTemplate) {
		velocityTemplate.setTimestamp(new Date());
		return velocityTemplateDAO.merge(velocityTemplate);
	}
	@Transactional(readOnly = false)
	public void velocityTemplate_remove(VelocityTemplate velocityTemplate) {
		velocityTemplateDAO.remove(velocityTemplate);
	}




	public Set<QuestionOption> questionOption_findAll()	throws DataAccessException {
		return questionOptionDAO.findAll();
	}
	public Set<QuestionOption> questionOption_findAll(int startResult,int maxRows) throws DataAccessException {
		return questionOptionDAO.findAll(startResult, maxRows);
	}
	public Long questionOption_getCount() {
		return questionOptionDAO.getCount();
	}
	public QuestionOption questionOption_findById(Long id) {
		return questionOptionDAO.findById(id);
	}

	public Set<QuestionOption> questionOption_findByQuestionId(Long id)	throws DataAccessException {
		return questionOptionDAO.findByQuestionId(id);
	}
	@Transactional(readOnly = false)
	public QuestionOption questionOption_merge(QuestionOption questionOption) {
		SortedSet<QuestionOption> updatedQuestionOptions = new TreeSet<QuestionOption>();
		Long questionId = questionOption.getQuestion().getId();
		Question question = questionDAO.findById(questionId);
		questionOption.setQuestion(question);
		Short order = question.updateSet(question.getOptions(), questionOption).getOrder();
		for (QuestionOption qo:  question.getOptions()) {
			updatedQuestionOptions.add(questionOptionDAO.merge(qo));
		}
		question.setOptions(updatedQuestionOptions);
		question =questionDAO.merge(question);
		return question.getElement(question.getOptions(), order);
	}
	@Transactional(readOnly = false)
	public void questionOption_remove(QuestionOption questionOption) {
		Question question = questionDAO.findById(questionOption.getQuestion().getId());
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
		Question question = questionDAO.findById(questionRowLabel.getQuestion().getId());
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
		Question question = questionDAO.findById(questionColumnLabel.getQuestion().getId());
		questionColumnLabelDAO.remove(questionColumnLabel);
		question.removeGaps(question.getOptions());
	}













	public Set<Question> question_findAll()	throws DataAccessException {
		return questionDAO.findAll();
	}
	public Set<Question> question_findAll(int startResult,	int maxRows) throws DataAccessException {
		return questionDAO.findAll(startResult, maxRows);
	}
	public Long question_getCount() {
		return questionDAO.getCount();
	}
	public Question question_findById(Long id) {
		return questionDAO.findById(id);
	}
	public Question question_findByOrder(Long surveyDefinitionId, Short pageOrder,Short questionOrder){
		return questionDAO.findByOrder(surveyDefinitionId,pageOrder,questionOrder);
	}

	@Transactional(readOnly = false)
	public Question question_merge(Question question) {

		Long dataSetId = null;
		if(question.getPage().getSurveyDefinition().getStatus().equals(SurveyDefinitionStatus.I)){
			if (question.getType().getIsDataSet()) {
				dataSetId=	question.getDataSetId();
				question.setType(QuestionType.SINGLE_CHOICE_DROP_DOWN);
				question.setDataSetId(null);
			}

			//Deleting options from question that do not support options
			if (!question.getSuportsOptions()){
				questionOptionDAO.deleteByQuestionId(question.getId());
			}
			SortedSet<Question> updatedQuestions = new TreeSet<Question>();
			Long pageId =question.getPage().getId();
			SurveyDefinitionPage surveyDefinitionPage = surveyDefinitionPageDAO.findById(pageId);
			question.setPage(surveyDefinitionPage);


			question.setDataSetId(null);

			Short order = surveyDefinitionPage.updateSet(surveyDefinitionPage.getQuestions(), question).getOrder();
			for (Question q:  surveyDefinitionPage.getQuestions()) {
				updatedQuestions.add(questionDAO.merge(q));
			}
			surveyDefinitionPage.setQuestions(updatedQuestions);
			surveyDefinitionPage = surveyDefinitionPageDAO.merge(surveyDefinitionPage);

			Question updatedquestion  =  surveyDefinitionPage.getElement(surveyDefinitionPage.getQuestions(), order);

			if (dataSetId != null) {
				Short o = 1;		
				for (DataSetItem dataSetItem :dataSetItemDAO.findByDataSetId(dataSetId)) {
					questionOptionDAO.merge(new QuestionOption(updatedquestion, o, dataSetItem.getValue(), dataSetItem.getText()));
					o++;
				}
			}

			return  updatedquestion;
		}
		else{

			Question dbQuestion= questionDAO.findById(question.getId());
			dbQuestion.setTip(question.getTip());
			dbQuestion.setQuestionText(question.getQuestionText());
			dbQuestion.setQuestionAnswer(question.getQuestionAnswer());
			return questionDAO.merge(dbQuestion);
		}

	}

	@Transactional(readOnly = false)
	public Question question_merge(Question question, SortedSet<QuestionOption> options) {
		//Clear data set field for non Dataset questions
		if (!question.getType().getIsDataSet()) {
			question.setDataSetId(null);
		}
		//Deleting options from question that do not support options
		if (!question.getSuportsOptions()){
			questionOptionDAO.deleteByQuestionId(question.getId());
		}

		SortedSet<Question> updatedQuestions = new TreeSet<Question>();
		Long pageId =question.getPage().getId();
		SurveyDefinitionPage surveyDefinitionPage = surveyDefinitionPageDAO.findById(pageId);
		question.setPage(surveyDefinitionPage);
		Short order = surveyDefinitionPage.updateSet(surveyDefinitionPage.getQuestions(), question).getOrder();
		for (Question q:  surveyDefinitionPage.getQuestions()) {
			updatedQuestions.add(questionDAO.merge(q));
		}
		surveyDefinitionPage.setQuestions(updatedQuestions);
		surveyDefinitionPage = surveyDefinitionPageDAO.merge(surveyDefinitionPage);

		Question q =  surveyDefinitionPage.getElement(surveyDefinitionPage.getQuestions(), order);

		for (QuestionOption questionOption:  options) {
			questionOption.setQuestion(q);
			questionOption = questionOptionDAO.merge(questionOption);
		}
		q.setOptions(options);
		return q;

	}









	@Transactional(readOnly = false)
	public Question question_updateOptions(Question question) {
		Long questionId =question.getId();
		Question q = questionDAO.findById(questionId);
		questionOptionDAO.deleteByQuestionId(questionId);
		SortedSet<QuestionOption> options = new TreeSet<QuestionOption>();
		for (QuestionOption option: question.getOptionsList2()) {

			if (option.getValue()!= null && option.getText()!=null &&  			
					option.getValue().trim().length() > 0 && option.getText().trim().length() > 0 ) {
				QuestionOption questionOption  = new QuestionOption(q,option.getOrder(),option.getValue(),option.getText());
				questionOption = questionOptionDAO.merge(questionOption);
				options.add(questionOption);
			}
		}
		q.setOptions(options);
		return questionDAO.merge(q);
	}


	@Transactional(readOnly = false)
	public Question question_updateColumnLabels(Question question) {
		Long questionId =question.getId();
		Question q = questionDAO.findById(questionId);
		questionColumnLabelDAO.deleteByQuestionId(questionId);
		SortedSet<QuestionColumnLabel> options = new TreeSet<QuestionColumnLabel>();
		for (QuestionColumnLabel columnLabel: question.getColumnLabelsList()) {

			if (columnLabel.getLabel()!= null &&  columnLabel.getLabel().trim().length() > 0 ) {
				QuestionColumnLabel questionColumnLabel  = new QuestionColumnLabel(q,columnLabel.getOrder(),columnLabel.getLabel());
				questionColumnLabel = questionColumnLabelDAO.merge(questionColumnLabel);
				options.add(questionColumnLabel);
			}
		}
		q.setColumnLabels(options);
		return questionDAO.merge(q);
	}


	@Transactional(readOnly = false)
	public Question question_updateRowLabels(Question question) {
		Long questionId =question.getId();
		Question q = questionDAO.findById(questionId);
		questionRowLabelDAO.deleteByQuestionId(questionId);
		SortedSet<QuestionRowLabel> options = new TreeSet<QuestionRowLabel>();
		for (QuestionRowLabel rowLabel: question.getRowLabelsList()) {

			if (rowLabel.getLabel()!= null &&  rowLabel.getLabel().trim().length() > 0 ) {
				QuestionRowLabel questionRowLabel  = new QuestionRowLabel(q,rowLabel.getOrder(),rowLabel.getLabel());
				questionRowLabel = questionRowLabelDAO.merge(questionRowLabel);
				options.add(questionRowLabel);
			}
		}
		q.setRowLabels(options);
		return questionDAO.merge(q);
	}


	@Transactional(readOnly = false)
	public Long question_remove(Long questionId){
		Question question = questionDAO.findById(questionId);
		Long pageId = question.getPage().getId();
		log.info("deleting  questionOptions");
		questionOptionDAO.deleteByQuestionId(question.getId());
		question.setOptions(null);
		questionDAO.remove(question);
		SurveyDefinitionPage surveyDefinitionPage = surveyDefinitionPageDAO.findById(question.getPage().getId());
		surveyDefinitionPage.removeGaps (surveyDefinitionPage.getQuestions());
		surveyDefinitionPageDAO.merge(surveyDefinitionPage);
		return pageId;

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


	public Set<Tags> tags_findAll() throws DataAccessException {
		return tagsDAO.findAll();
	}


	public Tags tags_findById(Long id) throws DataAccessException {
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


	public Set<QuestionBankOption> questionBankOption_findAll() throws DataAccessException {
		return questionBankOptionDAO.findAll();
	}

	public Set<QuestionBankOption> questionBankOption_findAll(int startResult, int maxRows) throws DataAccessException {
		return questionBankOptionDAO.findAll(startResult, maxRows);
	}

	public Long questionBankOption_getCount() {
		return questionBankOptionDAO.getCount();
	}

	public QuestionBankOption questionBankOption_findById(Long id) {
		return questionBankOptionDAO.findById(id);
	}

	public Set<QuestionBankOption> questionBankOption_findByQuestionId(QuestionBank qid) throws DataAccessException {
		return questionBankOptionDAO.findByQuestionId(qid);
	}

	@Transactional(readOnly = false)
	public QuestionBankOption questionBankOption_merge(QuestionBankOption questionOption) {
		SortedSet<QuestionBankOption> updatedQuestionOptions = new TreeSet<QuestionBankOption>();
		Long questionId = questionOption.getQuestion().getId();
		QuestionBank question = questionBankDAO.findById(questionId);
		questionOption.setQuestion(question);
		//Short order = question.updateSet(question.getOptions(), questionOption).getOrder();
		for (QuestionBankOption qo : question.getOptions()) {
			updatedQuestionOptions.add(questionBankOptionDAO.merge(qo));
		}
		question.setOptions(updatedQuestionOptions);
		question = questionBankDAO.merge(question);
		return question.getElement(question.getOptions(), new Short("2"));
	}

	@Transactional(readOnly = false)
	public void questionBankOption_remove(QuestionBankOption questionOption) {
		QuestionBank question = questionBankDAO.findById(questionOption.getQuestion().getId());
		questionBankOptionDAO.remove(questionOption);
		question.removeGaps(question.getOptions());
	}

	@Transactional(readOnly = true)
	public void questionBankOption_removeQuestionOptionsByQuestionId(Long id) {
		questionBankOptionDAO.deleteByQuestionId(id);
	}


	public Set<QuestionBank> questionBank_findAll() throws DataAccessException {
		return questionBankDAO.findAll();
	}

	public Set<QuestionBank> questionBank_findAll(int startResult, int maxRows) throws DataAccessException {
		return questionBankDAO.findAll(startResult, maxRows);
	}

	public Long questionBank_getCount() {
		return questionBankDAO.getCount();
	}

	public QuestionBank questionBank_findById(Long id) {
		return questionBankDAO.findById(id);
	}

	public QuestionBank questionBank_findByOrder(Long surveyDefinitionId, Short pageOrder, Short questionOrder) {
		return questionBankDAO.findByOrder(surveyDefinitionId, pageOrder, questionOrder);
	}


	@Transactional(readOnly = false)
	public QuestionBank questionBank_merge(QuestionBank question, SortedSet<QuestionBankOption> options) {
		//Clear data set field for non Dataset questions
		if (!question.getType().getIsDataSet()) {
			question.setDataSetId(null);
		}
		//Deleting options from question that do not support options
		if (!question.getSuportsOptions()) {
			questionBankOptionDAO.deleteByQuestionId(question.getId());
		}

		SortedSet<QuestionBank> updatedQuestions = new TreeSet<QuestionBank>();


		for (QuestionBankOption questionOption : options) {
			questionOption.setQuestion(question);
			questionOption = questionBankOptionDAO.merge(questionOption);
		}
		question.setOptions(options);
		return question;

	}


	@Transactional(readOnly = false)
	public QuestionBank questionBank_merge(QuestionBank question) {

		Long dataSetId = null;

		if (question.getType().getIsDataSet()) {
			dataSetId = question.getDataSetId();
			question.setType(QuestionType.SINGLE_CHOICE_DROP_DOWN);
			question.setDataSetId(null);
		}

		//Deleting options from question that do not support options
		if (!question.getSuportsOptions()) {
			questionBankOptionDAO.deleteByQuestionId(question.getId());
		}
		question = questionBankDAO.merge(question);

		question.setDataSetId(null);

		if (dataSetId != null) {
			Short o = 1;
			for (DataSetItem dataSetItem : dataSetItemDAO.findByDataSetId(dataSetId)) {
				questionBankOptionDAO.merge(new QuestionBankOption(question, o, dataSetItem.getValue(), dataSetItem.getText()));
				o++;
			}
		}
		return question;

	}

	@Transactional(readOnly = false)
	public Set<QuestionBank> findByTagAndDifficultyAndLimit(Tags tag, QuestionDifficultyLevel difficulty, Integer limit) {
		return questionBankDAO.findByTagAndDifficultyAndLimit(tag, difficulty, limit);
	}

	@Transactional(readOnly = false)
	public Set<QuestionHolder> findByTag(Tags tag) {
		return questionBankDAO.findByTag(tag);
	}

}
