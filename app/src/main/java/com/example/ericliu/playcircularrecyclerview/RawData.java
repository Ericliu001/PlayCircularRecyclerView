package com.example.ericliu.playcircularrecyclerview;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ericliu on 24/09/15.
 */
public final class RawData {
    private RawData(){}

    private static String[] fristNames =
            {
             "1. Eric"
             ,"2. Lucy"
             ,"3. John"
             , "4. Jordan"
                    ,"5. Marry"
            }
            ;
    private static String[] lastNames = {
            "Lee"
            ,"Brook"
            ,"Tom"
            ,"Tim"
            ,"Lehon"
    };


    public static List<Employee> getRawDataList(){
        List<Employee> list = new ArrayList();
        Employee employee;

        for (int i = 0; i < fristNames.length; i++) {
            employee = new Employee();
            employee.setFirstName(fristNames[i]);
            employee.setLastName(lastNames[i]);
            list.add(employee);
        }



        return list;
    }


}
