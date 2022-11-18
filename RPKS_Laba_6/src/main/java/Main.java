public class Main {
    static public void main(String[] args) {
        DateBase db = new DateBase();
        db.loadDateBases("student.csv", "grade.csv", "group.csv", "student.csv", "subject.csv");
        Table res = db.processQuery("SELECT student.full_name, grade FROM student, grade, subject WHERE grade.student_id = student.id AND grade.subject_id = subject.id AND subject_name = 'Math'");
        if (res != null) {
            System.out.println(res);
        }
    }
}
