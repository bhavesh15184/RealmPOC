package tal.realmpoc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import Model.Department;
import Model.Employee;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private Realm realm;
    private EditText mNameText;
    private EditText mDeptNameText;
    private EditText mAgeNameText;
    private TextView mCountTextView;
    private int empId = 1;
    private int mDeptId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNameText = (EditText) findViewById( R.id.nameEditText );
        mDeptNameText = (EditText) findViewById( R.id.deptEditText );
        mAgeNameText = (EditText) findViewById( R.id.ageEditText );
        mCountTextView = (TextView) findViewById( R.id.countTextView );

        try {
            Realm.init(this);

            realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(Employee.class);
            }
        });

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(Department.class);
            }
        });

            UpdateCounts();

            final Button button = (Button) findViewById(R.id.addButton);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    AddEmployee();

                    UpdateCounts();
                    ShowEmployeeList();
                }
            });
        }
        catch (Exception e)
        {
            realm.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close(); // Remember to close Realm when done.
    }

    private  void UpdateCounts()
    {
        String msg = "Total Counts: Employees- " + realm.where(Employee.class).count();
        msg = msg + ", Departments- " + realm.where(Department.class).count();
        mCountTextView.setText(msg);
    }
    private void AddEmployee()
    {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if(mNameText.getText().toString().length() > 0) {
                    Employee employee = realm.createObject(Employee.class);
                    employee.setId(empId++);
                    employee.setName(mNameText.getText().toString());
                    employee.setAge(mAgeNameText.getText().toString());
                    AddDepartment(employee);

                }

            }
        });
    }

    private void AddDepartment(Employee employee)
    {
        String name = mDeptNameText.getText().toString();
        if(name.length() > 0) {
            RealmResults<Department> depts = realm.where(Department.class).equalTo("name", name)
                    .findAll();
            if (depts.size() < 1) {
                Department department = realm.createObject(Department.class);
                department.setId(mDeptId++);
                department.setName(name);
                department.getEmployees().add(employee);
                employee.setDeptName(name);
            }
            else if(depts.size() == 1)
            {
                Department dept = depts.get(0);
                dept.getEmployees().add(employee);
                employee.setDeptName(name);
            }
        }
    }

    private void ShowEmployeeList()
    {
        RealmResults<Employee> employees = realm.where(Employee.class).findAll();
        String[] employeeDetails = new String[employees.size()];
        int i = 0;
        for (Employee e : employees) {
            employeeDetails[i++] = e.getName() + "(" + e.getDeptName() + ")";
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.employee_list,
                employeeDetails);

        ListView listView = (ListView) findViewById(R.id.employee_list);
        listView.setAdapter(adapter);
    }
}
