# Hadoop MapReduce Application: Student Examination Marks Analyzer

Big Data Analytics (CA 1) project implementing an Apache Hadoop MapReduce application in Java to calculate student total marks, average percentage, and final grades across multiple subjects, paired with an interactive browser-based pipeline simulator.

## Project Components
* **StudentMarksAnalysis.java** — Java MapReduce implementation containing MarksMapper, MarksReducer, and Job Driver classes.
* **student_marks.txt** — Sample CSV dataset formatted as RollNo,Name,Subject,Marks.
* **index.html** — Interactive web visualizer demonstrating InputSplit, Mapper, Shuffle & Sort, and Reducer stages in real time.

## Running on Apache Hadoop (HDFS & YARN)

1. Upload the dataset to HDFS:
   hdfs dfs -mkdir -p /user/hadoop/marks_input
   hdfs dfs -put student_marks.txt /user/hadoop/marks_input/

2. Compile the Java source and create the JAR file:
   javac -classpath $(hadoop classpath) -d . StudentMarksAnalysis.java
   jar -cvf student_marks.jar *.class

3. Execute the MapReduce job and view results:
   hadoop jar student_marks.jar StudentMarksAnalysis /user/hadoop/marks_input /user/hadoop/marks_output
   hdfs dfs -cat /user/hadoop/marks_output/part-r-00000