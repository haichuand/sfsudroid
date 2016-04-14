package woverines.sfsuapp.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import woverines.sfsuapp.R;
import woverines.sfsuapp.database.Course;
import woverines.sfsuapp.database.Event;

public class SchedulePlanner extends AppCompatActivity {

    private ScheduleListAdapter scheduleAdapter;
    public ArrayList<Course> courseArrayList;
    public ListView courseListView;
    public Dialog courseDetailDialog;

    TextView detailNameTV;
    TextView detailTimeTV;
    TextView detailNumberTV;
    TextView detailInstructorTV;
    Button detailCancelB;
    Button detailAddEventB;

//    private
    public ArrayList<Event> eventArray;

    //for testing
    Button genRadomClassesB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_planner);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //create array of myCourses
        courseArrayList = new ArrayList<>();




        genRadomClassesB = (Button) findViewById(R.id.gen_random_classes);
        genRadomClassesB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateClasses();
            }
        });



        courseListView = (ListView) findViewById(R.id.course_list_view);


        //SET UP REVIEW DIALOG
        courseDetailDialog = new Dialog(SchedulePlanner.this);

        courseDetailDialog.setContentView(R.layout.course_details_dialog);
        courseDetailDialog.setTitle("Course Options");

        //connecting TextViews to dialog
        detailNumberTV = (TextView) courseDetailDialog.findViewById(R.id.dialog_course_number);
        detailNameTV = (TextView) courseDetailDialog.findViewById(R.id.dialog_course_name);
        detailInstructorTV = (TextView) courseDetailDialog.findViewById(R.id.dialog_meet_time);
        detailTimeTV = (TextView) courseDetailDialog.findViewById(R.id.dialog_meet_time);

        //connecting Buttons to dialog
        detailCancelB = (Button) courseDetailDialog.findViewById(R.id.dialog_cancel_button);
        detailAddEventB = (Button) courseDetailDialog.findViewById(R.id.dialog_add_event);

        FloatingActionButton add_class_fab = (FloatingActionButton) findViewById(R.id.add_class_fab);
        add_class_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToClassCatalog = new Intent(v.getContext(), ClassCatalog.class);

                startActivity(goToClassCatalog);
            }
        });

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        generateClasses();
        displaySchedule();
     }

    public void generateClasses()
    {
//        if(scheduleAdapter.getCount() != 0)
            scheduleAdapter.clear();

        //generate random data for testing
        Course tempCourse = new Course(1, "668", "Advanced OOP", "Levine", "11:00 - 12:15", "somethign something...");
        courseArrayList.add(tempCourse);

        Random rnd = new Random();

        for(int i = 0; i < rnd.nextInt(15); i++)
        {
            tempCourse = new Course();
            tempCourse.genRandomCourse2();
            courseArrayList.add(tempCourse);
        }

        scheduleAdapter.notifyDataSetChanged();
    }


    public void displaySchedule() {

        //Populate the list
        if (scheduleAdapter == null) {  //if first time opening activity
            scheduleAdapter = new ScheduleListAdapter(this, R.layout.course_list_item, courseArrayList);
            courseListView.setAdapter(scheduleAdapter);
        } else {
            runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            scheduleAdapter.notifyDataSetChanged();
                        }
                    });
            return;
        }


        //creating onClick for each item in courseList
        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //setting up courseDetailsDialog
                detailNumberTV.setText(courseArrayList.get(position).getNumber());
                detailNameTV.setText(courseArrayList.get(position).getName());
                detailInstructorTV.setText(courseArrayList.get(position).getInstructor());
                detailTimeTV.setText(courseArrayList.get(position).getMeetTime());

                //add go to addEven activity
                detailAddEventB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        goToAddEvent();
                    }
                });

                //hide dialog button
                detailCancelB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        courseDetailDialog.dismiss();
                    }
                });

                //dialog has been prepared, display it.
                courseDetailDialog.show();
            }

        });
    }



    /**
     * Custom List Array Adapter
     *
     * @author Andrey Barsukov
     *         This is a custom adapter that uses a system of recycling a single view
     *         to fill up a scrollListView.
     *         <p/>
     *         This class uses a custom view holder class that stores only the nessesary values of
     *         a course that are required for a Course list view item.
     *         <p/>
     *         Layout Recycling system:
     *         - create a single instance of a ListView item layout course_list_itemml.
     *         if(ListView is empty){
     *         create a new instance of all views inside course_list_itemml
     *         create an instance of a ScheduleViewHolder class
     *         -set values of ScheduleViewHolder with the first course is courseList
     *         -attach each element of ScheduleViewHolder to its corresponding element in course_list_item.xml    *         -send the current instance of ScheduleViewHolder to the ListView Adapter
     *         }
     *         else{
     *         -set values of ScheduleViewHolder with the (position)th course is couseList
     *         -attach each element of ScheduleViewHolder to its corresponding element in course_list_itemml
     *         -send the current instance of ScheduleViewHolder to the ListView Adapter
     *         <p/>
     *         return the current instance of ScheduleViewHolder
     *         }
     *         <p/>
     *         RESULT: Only one instance of course_list_item.xmlnd each of its view is created
     *         -this since instance of it gets recycles for each aditional item in the ListView
     */
    private class ScheduleListAdapter extends ArrayAdapter<Course> {
        private int layout;      //stores references to recipe_list_item layouts

        public ScheduleListAdapter(Context context, int resource, ArrayList<Course> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        //setting up items in list
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //instead of creating a new course_list_item view, check if it exists,
            //if exits: reuse the view with new data - using .from(getContext()), context returned by super call
            //if does not exist: use inflater to create it (inflater is very taxing)
            ScheduleViewHolder mainViewHolder = null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ScheduleViewHolder viewHolder = new ScheduleViewHolder();

                //linking widgets
                viewHolder.number = (TextView) convertView.findViewById(R.id.course_number);
                //                viewHolder.clock = (ImageView) convertView.findViewById(R.id.recipe_list_item_clock);
                viewHolder.name = (TextView) convertView.findViewById(R.id.course_name);
                viewHolder.instructor = (TextView) convertView.findViewById(R.id.course_instructor);
                viewHolder.meetTime = (TextView) convertView.findViewById(R.id.course_meet_time);


                //setting info
                viewHolder.name.setText(getItem(position).getName().toString());
                viewHolder.number.setText(getItem(position).getNumber().toString());
                viewHolder.instructor.setText(getItem(position).getInstructor().toString());
                viewHolder.meetTime.setText(getItem(position).getMeetTime().toString());


                    /*
                    add a reference of this object into convertView, so we convertView != null
                        we can retrieve the object and directly set the new data to the ScheduleViewHolder items
                         (recyle this object)
                     */
                convertView.setTag(viewHolder);
            } else {  //converView != null --> retrieve viewHolder (using tag), assign it to externally declared ScheduleViewHolder (mainViewHolder)
                Course thisCourse = getItem(position);
                mainViewHolder = (ScheduleViewHolder) convertView.getTag();
                //manually set the data of list view items here//
                mainViewHolder.number.setText(thisCourse.getNumber());
                mainViewHolder.name.setText(thisCourse.getName());
                mainViewHolder.instructor.setText(thisCourse.getInstructor());
                mainViewHolder.meetTime.setText(thisCourse.getMeetTime());

            }

            return convertView;
            //return super.getView(position, convertView, parent);
        }
    }

    /**
     * Holds values for ListView adapter
     *
     * @author Andrey Barsukov
     *         viewHolder class
     *         used to hold the references of data in List<Course> array so we dont have to call the array
     *         items by id everytime.
     */

    public class ScheduleViewHolder {
        TextView number;
        TextView name;
        TextView instructor;
        TextView meetTime;
    }

}


