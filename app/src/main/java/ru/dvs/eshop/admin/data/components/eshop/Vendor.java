package ru.dvs.eshop.admin.data.components.eshop;

import java.util.HashMap;

/**
 * Производитель
 */
public class Vendor {
    int original_id;
    int id;
    boolean is_enabled;
    String title;
    HashMap<String, String> icon; //original, big, small
    String description;
    int ordering;

}
