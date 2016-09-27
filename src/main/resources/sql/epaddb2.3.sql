ALTER TABLE project_pluginparameter ADD COLUMN type varchar(20) NULL;
ALTER TABLE project_pluginparameter ADD COLUMN description varchar(150) NULL;

DROP TABLE if exists project_template;
CREATE TABLE project_template (id integer unsigned NOT NULL AUTO_INCREMENT,
project_id integer unsigned,
templatename varchar(128),
creator varchar(128),
createdtime timestamp,
updatetime timestamp,
updated_by varchar(64),
PRIMARY KEY (id)) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE UNIQUE INDEX uk_project_template_ind on project_template(project_id,templatename);


UPDATE dbversion SET version = '2.2';
commit;
