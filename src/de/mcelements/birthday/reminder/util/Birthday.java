package de.mcelements.birthday.reminder.util;

import de.mcelements.birthday.reminder.Main;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.*;

public class Birthday implements Comparable<Birthday>{

    private Date date;
    private String name;
    private String mail;
    private String phone;

    public Birthday(String date, String name, String mailOrPhone) {
        String[] dateFormat = new String[]{"dd.MM.yyyy", "d.M.y", "dd.MM."};
        for(String format : dateFormat){
            try {
                this.date = new SimpleDateFormat(format).parse(date);
                if(format.equals(dateFormat[2])){
                    Calendar c = Calendar.getInstance();
                    c.setTime(this.date);
                    c.set(YEAR, 1);
                    this.date = c.getTime();
                }
                break;
            } catch (ParseException e) {}
        }
        if(this.date == null){
            Main.LOGGER.warning("Wrong format for " + date + "! ("+name+")");
            this.date = new Date();
            this.date.setYear(0);
        }

        this.name = name;

        String[] mailPhone;
        if(mailOrPhone.contains("; ")){
            mailPhone = mailOrPhone.split("; ");
        } else {
            mailPhone = new String[]{mailOrPhone};
        }

        for(String s : mailPhone){
            if(s.matches("(^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$)")){
                mail = s;
            }else if(s.matches("^[0-9\\/-]+$")){
                phone = s;
            }
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

    public String getName(boolean toLowwerCase) {
        return toLowwerCase ? getName().toLowerCase() : getName();
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

    public String getListText(){
        return getListText(this.getCalendar(true).getTime().getTime() > System.currentTimeMillis());
    }

    public String getListText(boolean addYear){
        String contact = "";
        if(this.getMail() != null) {
            contact += "✉";
        }
        if(this.getPhone() != null) {
            contact += "✆";
        }
        if(!contact.isEmpty())
            contact = " ("+contact+")";

        if(getCalendar().get(YEAR) == 1){
            final SimpleDateFormat sdf = new SimpleDateFormat(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.list.no.year.date.format"));
            final String format = PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.list.no.year.format");
            return String.format(format, sdf.format(getDate()), getName())+contact;
        } else {
            final SimpleDateFormat sdf = new SimpleDateFormat(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.list.date.format"));
            final String format = PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.list.format");
            return String.format(format, sdf.format(getDate()), getAge(addYear), getName())+contact;
        }
    }

    @Override
    public String toString() {
        /*
        return "Name: " + getName() + ", Date: " + new SimpleDateFormat("dd.MM.yyyy").format(getDate()) + ", " +
                (!(getMail() == null || getMail().equals("")) ? "Mail: " + getMail() : "") +
                (!(getPhone() == null || getPhone().equals("")) ? " Phone: " + getPhone() : "");
        */
        return getListText();
    }

    @Override
    public int compareTo(Birthday o) {
        if(this.getCalendar(true).getTime().getTime() != o.getCalendar(true).getTime().getTime())
            return this.getCalendar(true).after(o.getCalendar(true)) ? 1 : -1;
        if(this.getCalendar().getTime().getTime() != o.getCalendar().getTime().getTime())
            return this.getCalendar().after(o.getCalendar()) ? 1 : -1;
        return getName().compareTo(o.getName());
    }
}
