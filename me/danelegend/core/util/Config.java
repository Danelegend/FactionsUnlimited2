package me.danelegend.core.util;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

import com.massivecraft.factions.P;

public class Config extends YamlConfiguration {

	private String fileName;
	private P plugin;

	public Config(P plugin, String fileName) {
		this(plugin, fileName, ".yml");
	}

	public Config(P plugin, String fileName, String fileExtension) {
		this.plugin = plugin;
		this.fileName = fileName + (fileName.endsWith(fileExtension) ? "" : fileExtension);
		this.createFile();
	}

	public String getFileName() {
		return this.fileName;
	}

	private void createFile() {
		File folder = P.p.getDataFolder();

		try {
			File file = new File(folder, this.fileName);

			if (!file.exists()) {
				if (P.p.getResource(this.fileName) != null) {
					P.p.saveResource(this.fileName, false);
				} else {
					this.save(file);
				}
				this.load(file);
			} else {
				this.load(file);
				this.save(file);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void save() {
		File folder = P.p.getDataFolder();

		try {
			this.save(new File(folder, this.fileName));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Config))
			return false;

		Config config = (Config) o;

		Label_0054: {
			if (this.fileName != null) {
				if (this.fileName.equals(config.fileName)) {
					break Label_0054;
				}
			} else if (config.fileName == null) {
				break Label_0054;
			}
			return false;
		}

		if (P.p != null) {
			if (!P.p.equals(config.plugin)) {
				return false;
			}
		}

		return true;
	}

	public int hashCode() {
		int result = (this.fileName != null) ? this.fileName.hashCode() : 0;
		result = 31 * result + ((this.plugin != null) ? this.plugin.hashCode() : 0);
		return result;
	}

}