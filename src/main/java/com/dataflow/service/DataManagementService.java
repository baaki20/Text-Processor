package com.dataflow.service;

import com.dataflow.model.TextRecord;
import java.util.*;

public class DataManagementService {

    private final Map<String, TextRecord> records = new HashMap<>();

    /**
     * Adds a new record to the collection.
     * @param record TextRecord to add
     * @return true if added, false if record with same ID exists
     */
    public boolean addRecord(TextRecord record) {
        if (records.containsKey(record.getId())) {
            return false;
        }
        records.put(record.getId(), record);
        return true;
    }

    /**
     * Updates an existing record.
     * @param record TextRecord with updated data
     * @return true if updated, false if no such record
     */
    public boolean updateRecord(TextRecord record) {
        if (!records.containsKey(record.getId())) {
            return false;
        }
        records.put(record.getId(), record);
        return true;
    }

    /**
     * Deletes a record by ID.
     * @param id record ID
     * @return true if deleted, false if no such record
     */
    public boolean deleteRecord(String id) {
        if (!records.containsKey(id)) {
            return false;
        }
        return records.remove(id) != null;
    }

    /**
     * Retrieves a record by ID.
     * @param id record ID
     * @return TextRecord or null
     */
    public TextRecord getRecord(String id) {
        return records.get(id);
    }

    /**
     * Returns all records
     * @return collection of records
     */
    public Collection<TextRecord> getAllRecords() {
        return records.values();
    }
}
