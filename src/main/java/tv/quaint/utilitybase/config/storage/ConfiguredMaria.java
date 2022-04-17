package tv.quaint.utilitybase.config.storage;

import de.leonhard.storage.sections.FlatFileSection;
import tv.quaint.utilitybase.PluginBase;

public class ConfiguredMaria {
    public String uri;
    public String name;
    public String host;
    public String port;
    public String user;
    public String pass;

    public String table;

    public ConfiguredMaria() {
        this.uri = "jdbc:mysql://%host%:%port%/%name%";
        this.name = "";
        this.host = "";
        this.port = "";
        this.user = "";
        this.pass = "";
        this.table = PluginBase.NAME + "_storage_data";
    }

    public ConfiguredMaria parseFromConfigSection(FlatFileSection section) {
        this.setDBInfo(section.getString("name"), section.getString("host"), section.getString("port"));
        this.setUserInfo(section.getString("user"), section.getString("pass"));
        this.setUri(section.getString("uri"));
        this.setTable(section.getString("table"));

        return this;
    }

    public ConfiguredMaria setDBInfo(String name, String host, String port) {
        this.name = name;
        this.host = host;
        this.port = port;

        return this;
    }

    public ConfiguredMaria setUserInfo(String user, String pass) {
        this.user = user;
        this.pass = pass;

        return this;
    }

    public ConfiguredMaria setUri(String uri) {
        this.uri = uri;

        return this;
    }

    public ConfiguredMaria setTable(String table) {
        this.table = table;

        return this;
    }

    public String getParsedUri() {
        return this.uri
                .replace("%host%", this.host)
                .replace("%port%", this.port)
                .replace("%name%", this.name)
                ;
    }
}
