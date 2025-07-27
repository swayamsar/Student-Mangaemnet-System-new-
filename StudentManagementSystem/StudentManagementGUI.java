
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

class Student {
    private String name, roll, grade, email;

    public Student(String name, String roll, String grade, String email) {
        this.name = name;
        this.roll = roll;
        this.grade = grade;
        this.email = email;
    }

    public String getRoll() {
        return roll;
    }

    public String toString() {
        return "Name: " + name + ", Roll: " + roll + ", Grade: " + grade + ", Email: " + email;
    }

    public String toFileString() {
        return name + "," + roll + "," + grade + "," + email;
    }

    public static Student fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length == 4)
            return new Student(parts[0], parts[1], parts[2], parts[3]);
        return null;
    }
}

class StudentManager {
    private ArrayList<Student> students = new ArrayList<>();
    private final String FILE_NAME = "students.txt";

    public StudentManager() {
        loadFromFile();
    }

    public void addStudent(Student s) {
        students.add(s);
        saveToFile();
    }

    public boolean removeStudent(String roll) {
        for (Student s : students) {
            if (s.getRoll().equalsIgnoreCase(roll)) {
                students.remove(s);
                saveToFile();
                return true;
            }
        }
        return false;
    }

    public Student searchStudent(String roll) {
        for (Student s : students) {
            if (s.getRoll().equalsIgnoreCase(roll)) return s;
        }
        return null;
    }

    public ArrayList<Student> getAllStudents() {
        return students;
    }

    private void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Student s : students)
                pw.println(s.toFileString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFromFile() {
        students.clear();
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                Student s = Student.fromFileString(sc.nextLine());
                if (s != null) students.add(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class StudentManagementGUI extends JFrame {
    private JTextField nameField, rollField, gradeField, emailField, searchRollField;
    private JTextArea displayArea;
    private StudentManager manager;

    public StudentManagementGUI() {
        manager = new StudentManager();
        setTitle("Student Management System");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Enter Student Details"));

        nameField = new JTextField();
        rollField = new JTextField();
        gradeField = new JTextField();
        emailField = new JTextField();

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Roll Number:"));
        inputPanel.add(rollField);
        inputPanel.add(new JLabel("Grade:"));
        inputPanel.add(gradeField);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(emailField);

        JButton addBtn = new JButton("Add Student");
        addBtn.addActionListener(e -> addStudent());
        inputPanel.add(addBtn);

        JButton showAllBtn = new JButton("Display All");
        showAllBtn.addActionListener(e -> displayAll());
        inputPanel.add(showAllBtn);

        add(inputPanel, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search or Remove Student"));

        searchRollField = new JTextField(10);
        JButton searchBtn = new JButton("Search");
        JButton removeBtn = new JButton("Remove");

        searchBtn.addActionListener(e -> searchStudent());
        removeBtn.addActionListener(e -> removeStudent());

        searchPanel.add(new JLabel("Roll Number:"));
        searchPanel.add(searchRollField);
        searchPanel.add(searchBtn);
        searchPanel.add(removeBtn);

        add(searchPanel, BorderLayout.CENTER);

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.SOUTH);

        setVisible(true);
    }

    private void addStudent() {
        String name = nameField.getText().trim();
        String roll = rollField.getText().trim();
        String grade = gradeField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || roll.isEmpty() || grade.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }

        manager.addStudent(new Student(name, roll, grade, email));
        JOptionPane.showMessageDialog(this, "Student added successfully!");

        nameField.setText("");
        rollField.setText("");
        gradeField.setText("");
        emailField.setText("");
    }

    private void searchStudent() {
        String roll = searchRollField.getText().trim();
        if (roll.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter roll number to search!");
            return;
        }

        Student s = manager.searchStudent(roll);
        if (s != null) {
            displayArea.setText("Student Found:
" + s.toString());
        } else {
            displayArea.setText("Student with roll " + roll + " not found.");
        }
    }

    private void removeStudent() {
        String roll = searchRollField.getText().trim();
        if (roll.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter roll number to remove!");
            return;
        }

        if (manager.removeStudent(roll)) {
            displayArea.setText("Student with roll " + roll + " removed successfully.");
        } else {
            displayArea.setText("Student not found.");
        }
    }

    private void displayAll() {
        StringBuilder sb = new StringBuilder();
        ArrayList<Student> list = manager.getAllStudents();
        if (list.isEmpty()) {
            sb.append("No students found.");
        } else {
            for (Student s : list) {
                sb.append(s.toString()).append("\n");
            }
        }
        displayArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        new StudentManagementGUI();
    }
}
