package de.mcelements.birthday.reminder.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class Birthday {

    private Date date;
    private String name;
    private String mail;
    private String phone;

    public Birthday(String date, String name, String mailOrPhone) {
        try {
            this.date = new SimpleDateFormat("dd.MM.yyyy").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.name = name;

        if(mailOrPhone.matches("(^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$)")){
            mail = mailOrPhone;
        }else if(mailOrPhone.matches("^[0-9\\/-]+$")){
            phone = mailOrPhone;
        }else if(mailOrPhone == null || mailOrPhone.equals("")){
        } else {
            System.err.println("no mail or phone: " + mailOrPhone);
        }
    }

    public Birthday setMail(String mail) {
        this.mail = mail;
        return this;
    }

    public Birthday setPhone(String phone) {
        this.phone = phone;
        return this;
    }


    public Date getDate() {
        return date;
    }

    public Calendar getCalendar(){
        return getCalendar(false);
    }

    public Calendar getCalendar(boolean changeYear){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if(changeYear) calendar.set(YEAR, Calendar.getInstance().get(YEAR));
        return calendar;
    }

    public String getName() {
        return name;
    }

    public String getMail() {
        return mail;
    }
    public String getMail(boolean notNull) {
        return mail == null && notNull ? "" : mail;
    }

    public String getPhone() {
        return phone;
    }
    public String getPhone(boolean notNull) {
        return phone == null && notNull ? "" : phone;
    }

    public int getAge(){
        return getAge(false);
    }
    public int getAge(boolean add){
        Calendar a = getCalendar(getDate());
        Calendar b = getCalendar(new Date());
        int diff = b.get(YEAR) - a.get(YEAR);
        if (a.get(MONTH) > b.get(MONTH) ||
                (a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
            diff--;
        }
        return diff + ((add) ? 1 : 0);//TODO
    }

    private Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }


    @Override
    public String toString() {
        return "Name: " + getName() + ", Date: " + new SimpleDateFormat("dd.MM.yyyy").format(getDate()) + ", " +
                (!(getMail() == null || getMail().equals("")) ? "Mail: " + getMail() : "") +
                (!(getPhone() == null || getPhone().equals("")) ? " Phone: " + getPhone() : "");
    }
}
