package ru.dvs.eshop.admin.data.components;

public abstract class ParentableModel extends Model {
    public int level;
    public int parentId;

    public ParentableModel(String controller, String type, String table) {
        super(controller, type, table);
    }

    public ParentableModel(String controller, String type, String table, int localId, int originalId) {
        super(controller, type, table, localId, originalId);
    }

}
