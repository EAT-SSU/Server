package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import ssu.eatssu.domain.menu.util.MenuNameSanitizer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class V32__backfill_menu_name_key extends BaseJavaMigration {

    private static final int BATCH_SIZE = 500;

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        try (Statement select = connection.createStatement();
             ResultSet menus = select.executeQuery("SELECT menu_id, name FROM menu WHERE name_key IS NULL");
             PreparedStatement update = connection.prepareStatement(
                     "UPDATE menu SET name_key = ? WHERE menu_id = ?")) {
            int pending = 0;
            while (menus.next()) {
                update.setString(1, MenuNameSanitizer.toKey(menus.getString("name")));
                update.setLong(2, menus.getLong("menu_id"));
                update.addBatch();
                if (++pending == BATCH_SIZE) {
                    update.executeBatch();
                    pending = 0;
                }
            }
            if (pending > 0) {
                update.executeBatch();
            }
        }
    }
}
