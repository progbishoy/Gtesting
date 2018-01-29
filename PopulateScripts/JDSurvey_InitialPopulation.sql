SET SQL_SAFE_UPDATES=0;

/*Question types
Not needed any more
delete from question_type;

INSERT INTO question_type (id, type_name, type_text, version)  VALUES (1,'BC','Yes No Checkbox',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (2,'BR','Yes No DropDown',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (3,'ST','Short Text Input',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (4,'LT','Long Text Input',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (5,'HT','Huge Text Input',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (6,'IN','Integer Input',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (7,'CR','Currency Input',0); 
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (8,'NM','Decimal Input',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (9,'DT','Date Input',0); 
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (10,'SD','Single choice Drop Down',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (11,'MC','Multiple Choice Checkboxes',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (12,'DD','DataSet Drop Down',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (13,'SR','Single Choice Radio Buttons',0);


INSERT INTO question_type (id, type_name, type_text, version)  VALUES (14,'BCM','Yes No Checkbox Matrix',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (15,'BRM','Yes No DropDown Matrix',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (16,'STM','Short Text Input Matrix',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (17,'INM','Integer Input Matrix',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (18,'CRM','Currency Input Matrix',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (19,'NMM','Decimal Input Matrix',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (20,'DTM','Date Input Matrix',0); 
*/





/*
delete from sec_user_group;
delete from sec_group_authority;
delete from sec_group;
delete from sec_authority;
delete from sec_user;
delete from regular_expression;
*/



/*Regular Expressions */
INSERT INTO regular_expression (id,description, name,regex ,version) VALUES (1,'First Name Validation', 'First Name','^[0-9a-zA-Z\\.\\-\\, ]{0,75}$', 0);
INSERT INTO regular_expression (id,description, name,regex ,version) VALUES (2,'Last Name Validation', 'Last Name','^[0-9a-zA-Z\\.\\-\\, ]{0,75}$', 0);
INSERT INTO regular_expression (id,description, name,regex ,version) VALUES (3,'Email Validation', 'Email','^([0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-\\w]*[0-9a-zA-Z]\\.)+[a-zA-Z]{0,9})$', 0);
INSERT INTO regular_expression (id,description, name,regex ,version) VALUES (4,'Address Validation', 'Address','^[0-9a-zA-Z\\.\\-,# ]{0,100}$', 0);
INSERT INTO regular_expression (id,description, name,regex ,version) VALUES (5,'Zip Code Validation', 'Zip Code','^\\d{5}$|^$', 0);
INSERT INTO regular_expression (id,description, name,regex ,version) VALUES (6,'US Phone Number Validation', 'US Phone Number','^\\d{3}-\\d{3}-\\d{4}$|^$', 0);
INSERT INTO regular_expression (id,description, name,regex ,version) VALUES (7,'Social Security Number Validation', 'Social Security Number','^(\\d{3}-\\d{2}-\\d{4}){1,12}$|^$', 0);

/*Security Authorities*/
INSERT INTO sec_authority (id,authority_type, name, description,version) VALUES (1,'I', 'ROLE_ADMIN', 'Highest level access withoyut anuy restrictions', 0);
INSERT INTO sec_authority (id,authority_type, name, description,version) VALUES (2,'I', 'ROLE_SURVEY_ADMIN','This role allows a user to manage surveys.', 0);
INSERT INTO sec_authority (id,authority_type, name, description,version) VALUES (3,'E', 'ROLE_SURVEY_PARTICIPANT','This role allows a user to fill a survey ', 0);


/*Security Groups*/
INSERT INTO sec_group (id,group_type,name,description,version) VALUES (1,'I','Adminitrators','Users with admininstator access',0);
INSERT INTO sec_group (id,group_type,name,description,version) VALUES (2,'I','Exam Adminitrators','Users who may manage exams',0);
INSERT INTO sec_group (id,group_type,name,description,version) VALUES (3,'E','Exam Participants','Users who can fill exams' ,0);

/*Security Group Authorities*/
INSERT INTO sec_group_authority (group_id,authority_id) VALUES (1,1);
INSERT INTO sec_group_authority (group_id,authority_id) VALUES (2,2);
INSERT INTO sec_group_authority (group_id,authority_id) VALUES (3,3);


/*Users*/
INSERT INTO sec_user (id,user_type,enabled, login,date_of_birth,		first_name,	middle_name,	last_name,	email,					creation_date,				last_update_date,		version,	password)
VALUES 				 (1, 'I',1,       'admin','1975-01-01',		'admin',	'admin',		'admin',	'me@example.com',	'2012-10-21 14:44:53',		'2012-10-21 14:44:53',	0,			'a601995c56b8c7148e36cf2feb682d308404e262a2dc6eab1a14e158ef6eed49');	


INSERT INTO sec_user_group (user_id,group_id) VALUES(1,1);



INSERT INTO velocity_template (id,`template_name`,template_definition,version,template_timestamp)
VALUES (1,'Forgot Login','<html><body><p><strong>Per your request below is your JD exam Login</strong></p><p>Login: ${user_login}</p></body></html>',0,'2012-10-21 14:44:53');

INSERT INTO velocity_template (id,`template_name`,template_definition,version,template_timestamp)
VALUES (2,'Forgot Password','<html><body><p>A password change request for your JD exam account has been made. If you have made this request please use the link below to change your password. If not please ignore this email.</p><p>${reset_password_link}</p></body></html>',0,'2012-10-21 14:44:53');

INSERT INTO velocity_template (id,`template_name`,template_definition,version,template_timestamp)
VALUES (3,'Internal Help Content','<html><body><h1 id="main_section">JD exam Frequently Asked Questions</h1>
<!-- EXAM creation FAQs -->
<h2>EXAM Process</h2>
<ul>
<li><a href="#EXAM_sec1">How do I create exams?</a></li>
<li><a href="#EXAM_sec2">What are the two templates that appear below the description box in the create new EXAM window?</a></li>
<li><a href="#EXAM_sec3">How do I make questions require an answer before proceeding any further into the EXAM?</a></li>
<li><a href="#EXAM_sec4">How do I attach logos to exams?</a></li>
<li><a href="#EXAM_sec5">What are the steps to fill out exams?</a></li>
<li><a href="#EXAM_sec6">How do I change the theme or color of exams?</a></li>
</ul>
<!-- Logic FAQs -->
<h2>Logic Features</h2>
<ul>
<li><a href="#logic_sec1">What does the randomize questions and options feature do?</a></li>
<li><a href="#logic_sec2">How do I apply branching to questions and answers?</a></li>
<li><a href="#logic_sec3">What is the piping of answers feature and how do I use it?</a></li>
</ul>
<!-- Statistics FAQs -->
<h2>Data Collection</h2>
<ul>
<li><a href="#data_sec1">How do I download copies of completed exams?</a></li>
<li><a href="#data_sec2">How can I view EXAM data/statistics?</a></li>
<li><a href="#data_sec3">What options are available for exporting EXAM statistics?</a></li>
</ul>
<!-- Security FAQs -->
<h2>Security</h2>
<ul>
<li><a href="#security_sec1">How do I create users?</a></li>
<li><a href="#security_sec2">Where is the option to make exams private or public?</a></li>
<li><a href="#security_sec3">When I try to log in I am getting a message that states Your login attempt was not successful, try again. Reason: User is disabled. Why is this occurring?</a></li>
</ul>
<!-- Other FAQs -->
<h2>Other Features</h2>
<ul>
<li><a href="#other_sec1">How do I export and import exams?</a></li>
<li><a href="#other_sec2">What are datasets?</a></li>
<li><a href="#other_sec3">How do I send out EXAM invitations to a group of participants?</a></li>
<li><a href="#other_sec4">What are masks?</a></li>
</ul>
<!-- EXAM_sec1 -->
<h2 id="EXAM_sec1">How do I create EXAMs?</h2>
<p style="color: black;">In order to create exams you must either have administrator or exam administrator privileges. Follow these steps to create a exam:</p>
<ol style="padding: 0em 4em;">
<li>Go to the Settings tab and click on exams.</li>
<li>On the left panel click on Add new exam.</li>
<li>Select a department and type in a name for the exam. Fill out any of the other optional fields on the page and click on Save.</li>
<li>Click on Add new Page from the left panel or from the bottom right corner.</li>
<li>Select an order, enter a title for the exam page, include any additional instructions, and determine whether to randomize the questions or not.</li>
<li>At the bottom right corner click on add question.</li>
<li>Choose a position order, question type, and type in the question text. Fill out any other fields if necessary and click on Save.</li>
<li>Depending on the question type additional fields may need to be defined:<ol type="a">
<li>An update options link appears for single choice drop down, multiple choice checkboxes, and single choice radio button questions types</li>
<li>The update columns and update rows links appears for all matrix questions</li>
</ol></li>
<li>Add more questions to the page and exam pages if necessary.</li>
<li>After building the exam click on Publish exam from the panel on the left to publish or Export exam to download it.</li>
</ol><!-- exam_sec2 -->
<h2 id="exam_sec2">What are the two templates that appear below the description box in the create new exam window?</h2>
<p style="color: black;">The first template, Email Invitation Template, contains the message that will be displayed to the email recipients for email invitation exams. The second template, Completed EXAM Template, is the message displayed to participants after completing a EXAM. Both of these templates can be modified with custom messages.</p>
<!-- exam_sec3 -->
<h2 id="exam_sec3">How do I make questions require an answer before proceeding any further into the exam?</h2>
<p style="color: black;">While creating or updating questions there is a checkbox option labeled as Mandatory. Enabling this option will prevent participants from advancing to the next page unless an answer is provided.</p>
<!-- exam_sec4 -->
<h2 id="exam_sec4">How do I attach logos to exams?</h2>
<p style="color: black;">Adding customized or company logos to exams can be accomplished during the creation process or after they have been published. Follow these steps to add a logo:</p>
<ol style="padding: 0em 4em;">
<li>From the top menu bar go to Settings and click on exams.</li>
<li>Select a EXAM to add the logo and click on the Show EXAM link under the Actions column.</li>
<li>From the left panel click on Update Logo.</li>
<li>Click on the Choose File button, select an image (supported types include png, gif, bmp, and jpeg), and click on Upload.</li>
</ol><!-- exam_sec5 -->
<h2 id="exam_sec5">What are the steps to fill out exams?</h2>
<p style="color: black;">Once a EXAM has been published it is now ready to be filled out by participants. If the EXAM has been made public then registering participants is not necessary. However, if the EXAM has been made private then an administrator needs to assign it to the appropriate participants.</p>
<ol style="padding: 0em 4em;">
<li>Log in to the external site.</li>
<li>Locate the EXAM to be filled and click on the Fill EXAM link.</li>
<li>If the EXAM is being filled out for the first time then a new EXAM will automatically be generated. Otherwise, click on the Create New EXAM link located on the bottom right corner of the page.</li>
<li>After completing a page click on Next to proceed to the next page.</li>
<li>Review the EXAM on the final page and click on Submit to finish.</li>
</ol><!-- exam_sec6 -->
<h2 id="exam_sec6">How do I change the theme or color of exams?</h2>
<p style="color: black;">The color and overall look of EXAMs may be modified by selecting a different theme in the Create new EXAM or Update EXAM windows. Clicking on the Lookup Theme link located at the right of the theme options will display a preview of each theme.</p>
<!-- logic_sec1 -->
<h2 id="logic_sec1">What does the randomize questions and options feature do?</h2>
<p style="color: black;">The randomize feature sorts a list of questions and answers in a randomized order. This allows for a EXAM to display questions and options in a different order each time. The randomization feature is found in the create/update EXAM page window and in the create/update questions window.</p>
<!-- logic_sec2 -->
<h2 id="logic_sec2">How do I apply branching to questions and answers?</h2>
<p style="color: black;">Branching is a method for skipping irrelevant questions based upon answer entries. While filling out EXAMs, there may be instances where particular answers require a participant to skip to specific questions or sections witin the EXAM. To add branching logic to questions follow these steps:</p>
<ol style="padding: 0em 4em;">
<li>Choose a EXAM page containing the questions to add the branching logic.</li>
<li>Click on the Edit branch and skip logic icon located inside the page header.</li>
<li>Select the page to skip to in the Go To drop down list.</li>
<li>In the When section choose a condition to be applied.</li>
<li>Enable the questions to apply the logic to.</li>
<li>Under the Values section, pick the answers that will activate the branch.</li>
<li>Click on Save.</li>
</ol><!-- logic_sec3 -->
<h2 id="logic_sec3">What is the piping of answers feature and how do I use it?</h2>
<p style="color: black;">The piping feature allows selected answers from questions to be incorporated into other questions. To utilize te piping functionality a parameter must be added to the question text. The parameter consists of a dollar sign, opening curly brace, page number, question number, and closing curly brace, written in that order. So for example, if we want to use te answer from question 4 on page 2, we would add the following parameter inside the current question text: ${p2q4}.</p>
<p style="color: black;"><em>Note: The piping feature is only operational by referencing answers from preceding pages. Attempting to use an answer from a future page will not work.</em></p>
<!-- data_sec1 -->
<h2 id="data_sec1">How do I download copies of completed exams?</h2>
<p style="color: black;">exam entries may be downloaded as PDF files for viewing, printing, and keeping copies of them. To download exam entries do the following:</p>
<ol style="padding: 0em 4em;">
<li>Go to EXAM Entries at the top menu bar and click on List.</li>
<li>Select a EXAM and click on Show Entries.</li>
<li>Click on the Show EXAM link associated with the EXAM to be downloaded.</li>
<li>On the left panel click on the Export to PDF tab.</li>
<li>The EXAM is displayed as a PDF and can be printed or saved.</li>
</ol><!-- data_sec2 -->
<h2 id="data_sec2">How can I view EXAM data/statistics?</h2>
<p style="color: black;">Statistics may be viewed in the application. Questions, separated into their respective pages, are displayed in the left panel while the data is shown in the right panel. EXAM statistics may be viewed by doing the following:</p>
<ol style="padding: 0em 4em;">
<li>Click on the Statistics tab located on the top menu bar.</li>
<li>Select a EXAM and click on Show Statistics.</li>
</ol><!-- data_sec3 -->
<h2 id="data_sec3">What options are available for exporting EXAM statistics?</h2>
<p style="color: black;">There are three different formats to export EXAM statistics for data analysis. EXAM statistics may be exported as an Excel file, comma delimited file, or SPSS file. As stated in the description, use the comma delimited values or SPSS formats for EXAMs containing large amounts of data. To export EXAM statistics complete these steps:</p>
<ol style="padding: 0em 4em;">
<li>Under EXAM Entries click on the Export link.</li>
<li>Locate the EXAM that is to be exported from the list.</li>
<li>Choose a format to export the EXAM as by clicking on one of the links under the Actions column.</li>
<li>Specify a directory and file name and click on Save.</li>
</ol><!-- security_sec1 -->
<h2 id="security_sec1">How do I create users?</h2>
<p style="color: black;">The process of creating both internal and external users may only be accomplished by administrators. To create new internal users follow these steps:</p>
<ol style="padding: 0em 4em;">
<li>Ensure you are logged in as an administrator.</li>
<li>From the top menu bar go to the Security tab and click on Internal Users.</li>
<li>Click on Add new Internal User from the left panel.</li>
<li>On the Create new User page, click on the Enabled option to activate the account or leave it blank if the account is to remain deactivated.</li>
<li>Fill in the rest of the required fields. <br /><em>Note: The date of birth field accepts inputs in the MM/DD/YYYY format. The password must be at least eight characters long, contain at least one number, one lower case letter, one upper case letter, and at least one of these special characters: [@#$%^&amp;+=]</em></li>
<li>Select a group to place the user under.</li>
<li>If the assigned group is EXAM Administrators, then choose a department or departments the user will have access to.</li>
</ol>
<p style="color: black;">The method of creating a new external user is essentially identical, except that the only option under the Groups section is EXAM Participants. Also, instead of having departmental access external users only have access to EXAMs.</p>
<!-- security_sec2 -->
<h2 id="security_sec2">Where is the option to make EXAMs private or public?</h2>
<p style="color: black;">The option to make exams private or public is located in the Create new EXAM and Update EXAM windows. The checkbox is labeled as Available to public.</p>
<!-- security_sec3 -->
<h2 id="security_sec3">When I try to log in I am getting a message that states Your login attempt was not successful, try again. Reason: User is disabled. Why is this occurring?</h2>
<p style="color: black;">This message is displayed when a user attempts to sign into the application with an inactive account. An administrator can remedy this issue by:</p>
<ol style="padding: 0em 4em;">
<li>Go to the Security tab on the top menu bar.</li>
<li>Depending on the user account, click on Internal or External Users.</li>
<li>Find the disabled account and click on the Update User link under the Actions column.</li>
<li>Check the Enbaled checkbox and click on Save.</li>
</ol><!-- other_sec1 -->
<h2 id="other_sec1">How do I export and import exams?</h2>
<p style="color: black;">Exporting exams only require a few steps:</p>
<ol style="padding: 0em 4em;">
<li>Go to the Settings tab and click on exams.</li>
<li>Find the EXAM to export and click on the Show EXAM link located under the Actions column.</li>
<li>From the left panel click on Export EXAM.</li>
<li>Choose a directory and specify a name for the file.</li>
</ol>
<p style="color: black;">To import EXAMs follow these steps:</p>
<ol style="padding: 0em 4em;">
<li>Go the Settings tab and click on EXAMs.</li>
<li>Click on Import EXAMs from the left panel.</li>
<li>Select a department, enter a name for the EXAM, and click on Choose File.</li>
<li>Browse to the directly containing the EXAM file and open it.</li>
<li>Click on Upload.</li>
</ol>
<p style="color: black;">An alternative method for importing EXAMs is available, but is restricted to administrators only:</p>
<ol style="padding: 0em 4em;">
<li>Go to the Security tab and click on Departments.</li>
<li>Select a department and click on the show department link under the Actions column.</li>
<li>Click on Import EXAMs from the left panel.</li>
<li>Type in a name for the EXAM.</li>
<li>Select the EXAM file and click on Upload.</li>
</ol><!-- other_sec2 -->
<h2 id="other_sec2">What are datasets?</h2>
<p style="color: black;">Datasets are collections of data that can be imported and then used to populate a list of options. The accepted format of datasets is the comma delimited values file. An example dataset file is available for download on the Upload Dataset page. Please note that the process of adding, updating, and deleting datasets is restricted to administrators only. Do the following to add datasets:</p>
<ol style="padding: 0em 4em;">
<li>Under the Settings tab click on Datasets.</li>
<li>Click on Add new Dataset from the</li>
</ol><!-- other_sec3 -->
<h2 id="other_sec3">How do I send out EXAM invitations to a group of participants?</h2>
<p style="color: black;">A comma delimited file containing a list of the EXAM participants information may be used to send out email invitations for EXAMs. The format for the comma delimited files is first name, middle name, last name, and email address. An example file may be downloaded on the Invite Participants page.</p>
<p style="color: black;"><em>Note: EXAM invitations may only be performed by administrators.</em></p>
<p style="color: black;">The steps to send out EXAM invitations are:</p>
<ol style="padding: 0em 4em;">
<li>Click on the Invitations link located under the Settings tab.</li>
<li>Click on Invite Participants from the left panel.</li>
<li>Select a EXAM and choose an invitations file.</li>
<li>Click on Upload.</li>
</ol>
<p style="color: black;">The total number of email invitations sent and number of invitation emails opened is displayed, along with more detailed information per EXAM.</p>
<!-- other_sec4 -->
<h2 id="other_sec4">What are masks?</h2>
<p style="color: black;">Masks are text patterns that responses must adhere to. 
If a mask is applied to a question then a participants entry to it is required to match the specific format of the mask. Only administrators are allowed to add, update, and delete masks. Applying them to questions may be carried out during the question creation or update processes under the Validation Information section.</p></body></html>'
,0,'2012-10-21 14:44:53');

INSERT INTO velocity_template (id,`template_name`,template_definition,version,template_timestamp)
VALUES (4,'Exam Invitation Email','<html><body><h2>Invitation</h2> <p>Hi ${full_name};</p> <p>You have been invited to participate in the folllowing EXAM:<br />EXAM: ${EXAM_name}<br />Your Password :${password}<br />To participate please click on the following link:</p> <p>${EXAM_link}</p></body></html>',0,'2012-10-21 14:44:53');


INSERT INTO velocity_template (id,`template_name`,template_definition,version,template_timestamp)
VALUES (5,'Exam Completed Page Content','Your EXAM has been submitted. Thank you for participating.',0,'2012-10-21 14:44:53');

INSERT INTO day (id,day_name,version) VALUES (1,'Monday',1);
INSERT INTO day (id,day_name,version) VALUES (2,'Tuesday',1);
INSERT INTO day (id,day_name,version) VALUES (3,'Wednesday',1);
INSERT INTO day (id,day_name,version) VALUES (4,'Thursday',1);
INSERT INTO day (id,day_name,version) VALUES (5,'Friday',1);
INSERT INTO day (id,day_name,version) VALUES (6,'Saturday',1);
INSERT INTO day (id,day_name,version) VALUES (7,'Sunday',1);




INSERT INTO global_settings (id, invalid_content_message, invalid_file_size_message, maximun_file_size, password_enforcement_message, password_regex, valid_content_types, valid_image_types, version) 
VALUES ('1', 'The type of file that you are trying to upload is invalid. Please upload a valid file.', 'The size of the file that you are trying to upload is invalid. Please upload a file that does not exceed 10MB.', '10', 'Your password must be at least 8 characters long. It must have at least one numeric character, one uppercase, one lowercase and one symbol.', '(?=.{8,})(?=.*?[^\\\\w\\\\s])(?=.*?[0-9])(?=.*?[A-Z]).*?[a-z].*', 'image/gif,image/jpeg,image/pjpeg,image/png,image/tiff,application/pdf,application/vnd.oasis.opendocument.text,application/vnd.oasis.opendocument.spreadsheet,application/vnd.oasis.opendocument.presentation,application/vnd.oasis.opendocument.graphics,application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-powerpoint,application/vnd.openxmlformats-officedocument.presentationml.presentation,application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'image/gif,image/jpeg,image/pjpeg,image/png,image/tiff', '0');





















