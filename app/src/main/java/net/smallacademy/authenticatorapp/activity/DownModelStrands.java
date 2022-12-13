package net.smallacademy.authenticatorapp.activity;

public class DownModelStrands {

    String Name,Link,Date;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }
    public DownModelStrands(String Name, String Link, String Date){
        this.Link=Link;
        this.Name=Name;
        this.Date=Date;

    }

}
