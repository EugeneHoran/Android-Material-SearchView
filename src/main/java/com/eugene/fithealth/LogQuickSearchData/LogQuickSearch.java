package com.eugene.fithealth.LogQuickSearchData;

import android.content.Context;

import com.eugene.fithealth.AppActivity;
import com.orm.androrm.CharField;
import com.orm.androrm.DateField;
import com.orm.androrm.Filter;
import com.orm.androrm.Model;
import com.orm.androrm.QuerySet;

import java.util.Date;
import java.util.List;

public class LogQuickSearch extends Model {

    protected CharField meal_name;
    protected DateField date;

    public LogQuickSearch() {
        super(true);
        meal_name = new CharField();
        date = new DateField();
    }

    public static LogQuickSearch create(String mealName, Date mDate) {
        LogQuickSearch LogQuickSearch = new LogQuickSearch();
        LogQuickSearch.setName(mealName);
        LogQuickSearch.setDate(mDate);
        LogQuickSearch.save();
        return LogQuickSearch;
    }


    public String getName() {
        return meal_name.get();
    }

    public void setName(String count) {
        meal_name.set(count);
    }

    public void setDate(Date d) {
        date.set(d);
    }

    public Date getDate() {
        return date.get();
    }

    private static String formatProjectForQuery(String name) {
        String name1 = name;
        return name1;
    }

    public static List<LogQuickSearch> logSortByProjectType(String Key_) {
        String query_string = formatProjectForQuery(Key_);
        Filter filter = new Filter();
        filter.contains("meal_name", query_string);
        return LogQuickSearch.objects().filter(filter).orderBy("meal_name").toList();
    }

    public boolean save() {
        int id = LogQuickSearch.objects(context(), LogQuickSearch.class).all().count() + 1;
        return this.save(context(), id);
    }

    public boolean edit() {
        return this.save(context());
    }

    public boolean delete() {
        return this.delete(context());
    }

    public static List<LogQuickSearch> all() {

        return LogQuickSearch.objects().all().orderBy("-date").toList();
    }

    public static List<LogQuickSearch> FilterByName(String name) {
        Filter filter = new Filter();
        filter.contains("meal_name", name);
        return LogQuickSearch.objects().filter(filter).orderBy("meal_name").toList();
    }

    public static QuerySet<LogQuickSearch> objects() {

        return LogQuickSearch.objects(context(), LogQuickSearch.class);
    }

    /**
     * Get application context
     *
     * @return
     */
    private static Context context() {

        return AppActivity.context();
    }
}
