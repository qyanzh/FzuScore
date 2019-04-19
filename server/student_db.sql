DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS course_select;

CREATE TABLE IF NOT EXISTS students(
  student_id INTEGER UNSIGNED PRIMARY KEY,
  student_name CHAR(8) NOT NULL,
  student_permission TINYINT UNSIGNED DEFAULT 0,
  student_password VARCHAR(128) NOT NULL,
  student_term_start INTEGER UNSIGNED NOT NULL,
  student_term_end INTEGER UNSIGNED NOT NULL
)character set = utf8;

CREATE TABLE IF NOT EXISTS courses(
  course_id INTEGER UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  course_name VARCHAR(30) NOT NULL
)character set = utf8;

CREATE TABLE IF NOT EXISTS course_select(
  course_id INTEGER UNSIGNED,
  student_id CHAR(8) NOT NULL,
  course_score TINYINT UNSIGNED DEFAULT NULL,
  term INTEGER UNSIGNED NOT NULL,
  course_rank TINYINT UNSIGNED
)character set = utf8;

CREATE TABLE IF NOT EXISTS course_stat(
  course_id INTEGER UNSIGNED,
  course_average DECIMAL(4,2),
  course_number INTEGER UNSIGNED,
  course_max TINYINT UNSIGNED,
  course_min TINYINT UNSIGNED,
  course_perfect DECIMAL(4,2),
  course_pass DECIMAL(4,2),
  FOREIGN KEY course_id references courses(course_id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS student_rank(
  student_id INTEGER UNSIGNED,
  term INTEGER UNSIGNED,
  average_score DECIMAL(4,2),
  rank TINYINT UNSIGNED
);