package com.massivecraft.factions;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;

import mkremins.fanciful.FancyMessage;

public class Perm {

	// Order Enemy, Neutral, Truce, Ally, Member, moderator, coleader, leader
	private boolean[] buildBlocks = new boolean[] { false, false, false, false, true, true, true, true };
	private boolean[] destroyBlocks = new boolean[] { false, false, false, false, true, true, true, true };
	private boolean[] fly = new boolean[] { false, false, false, false, true, true, true, true };
	private boolean[] interact = new boolean[] { false, false, false, false, true, true, true, true };
	private boolean[] logout = new boolean[] { false, false, false, false, true, true, true, true };
	private boolean[] homes = new boolean[] { false, false, false, false, true, true, true, true };
	private boolean[] tpothers = new boolean[] { false, false, false, false, true, true, true, true };
	private boolean[] missions = new boolean[] { false, false, false, false, true, true, true, true };

	public boolean hasPermission(FPlayer fp, Faction factionAt, String permission) {
		if (fp.getFaction() == factionAt) {
			if (fp.getRole() == Role.NORMAL)
				return hasPermission(permission, "member");
			if (fp.getRole() == Role.MODERATOR)
				return hasPermission(permission, "moderator");
			if (fp.getRole() == Role.COLEADER)
				return hasPermission(permission, "coleader");
			if (fp.getRole() == Role.ADMIN)
				return hasPermission(permission, "leader");
		} else {
			if (fp.getFaction().getRelationTo(factionAt) == Relation.ENEMY)
				return hasPermission(permission, "enemy");
			if (fp.getFaction().getRelationTo(factionAt) == Relation.NEUTRAL)
				return hasPermission(permission, "neutral");
			if (fp.getFaction().getRelationTo(factionAt) == Relation.TRUCE)
				return hasPermission(permission, "truce");
			if (fp.getFaction().getRelationTo(factionAt) == Relation.ALLY)
				return hasPermission(permission, "ally");
		}
		return false;
	}

	public boolean hasPermission(String permission, String relation) {
		return getPermission(permission)[parseRelation(relation)];
	}

	public void setPermission(String permission, String relation, boolean allow) {
		getPermission(permission)[parseRelation(relation)] = allow;
	}

	public boolean[] getPermission(String permission) {
		if (permission.equalsIgnoreCase("build")) {
			return buildBlocks;
		} else if (permission.equalsIgnoreCase("destroy")) {
			return destroyBlocks;
		} else if (permission.equalsIgnoreCase("fly")) {
			return fly;
		} else if (permission.equalsIgnoreCase("interact")) {
			return interact;
		} else if (permission.equalsIgnoreCase("logout")) {
			return logout;
		} else if (permission.equalsIgnoreCase("homes")) {
			return homes;
		} else if (permission.equalsIgnoreCase("tpothers")) {
			return tpothers;
		} else if (permission.equalsIgnoreCase("missions")) {
			return missions;
		}
		return null;
	}

	public int parseRelation(String relation) {
		if (relation.equalsIgnoreCase("enemy")) {
			return 0;
		} else if (relation.equalsIgnoreCase("neutral")) {
			return 1;
		} else if (relation.equalsIgnoreCase("truce")) {
			return 2;
		} else if (relation.equalsIgnoreCase("ally")) {
			return 3;
		} else if (relation.equalsIgnoreCase("member")) {
			return 4;
		} else if (relation.equalsIgnoreCase("moderator")) {
			return 5;
		} else if (relation.equalsIgnoreCase("coleader")) {
			return 6;
		} else if (relation.equalsIgnoreCase("leader")) {
			return 7;
		} else {
			return 0;
		}
	}

	public void sendPermissionsList(Player p) {
		p.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "----------" + ChatColor.GREEN
				+ "Perms for your faction" + ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "----------");
		p.sendMessage(ChatColor.RED + "ENE " + ChatColor.WHITE + "NEU " + ChatColor.LIGHT_PURPLE + "TRU "
				+ ChatColor.DARK_PURPLE + "ALL " + ChatColor.GREEN + "MEM " + ChatColor.GREEN + "MOD " + ChatColor.GREEN
				+ "COL " + ChatColor.GREEN + "LEA ");
		details("build").send(p);
		details("destroy").send(p);
		details("fly").send(p);
		details("interact").send(p);
		details("logout").send(p);
		details("homes").send(p);
		details("tpothers").send(p);
		details("missions").send(p);
	}

	public FancyMessage details(String action) {
		FancyMessage message = new FancyMessage();

		message = message.text(formatBoolean(hasPermission(action, "enemy"))).tooltip("Set to " + formatOppositeBoolean(hasPermission(action, "enemy"))).command("/f perm set " + action + " enemy " + getOpposite(hasPermission(action, "enemy")));
		message = message.then(" " + formatBoolean(hasPermission(action, "neutral"))).tooltip("Set to " + formatOppositeBoolean(hasPermission(action, "neutral"))).command("/f perm set " + action + " neutral " + getOpposite(hasPermission(action, "neutral")));
		message = message.then(" " + formatBoolean(hasPermission(action, "truce"))).tooltip("Set to " + formatOppositeBoolean(hasPermission(action, "truce"))).command("/f perm set " + action + " truce " + getOpposite(hasPermission(action, "truce")));
		message = message.then(" " + formatBoolean(hasPermission(action, "ally"))).tooltip("Set to " + formatOppositeBoolean(hasPermission(action, "ally"))).command("/f perm set " + action + " ally " + getOpposite(hasPermission(action, "ally")));
		message = message.then(" " + formatBoolean(hasPermission(action, "member"))).tooltip("Set to " + formatOppositeBoolean(hasPermission(action, "member"))).command("/f perm set " + action + " member " + getOpposite(hasPermission(action, "member")));
		message = message.then(" " + formatBoolean(hasPermission(action, "moderator"))).tooltip("Set to " + formatOppositeBoolean(hasPermission(action, "moderator"))).command("/f perm set " + action + " moderator " + getOpposite(hasPermission(action, "moderator")));
		message = message.then(" " + formatBoolean(hasPermission(action, "coleader"))).tooltip("Set to " + formatOppositeBoolean(hasPermission(action, "coleader"))).command("/f perm set " + action + " coleader " + getOpposite(hasPermission(action, "coleader")));
		message = message.then(" " + formatBoolean(hasPermission(action, "leader"))).tooltip("Set to " + formatOppositeBoolean(hasPermission(action, "leader"))).command("/f perm set " + action + " leader " + getOpposite(hasPermission(action, "leader")));
		message = message.then(" " + ChatColor.WHITE + action);
		
		return message;
	}

	public String getOpposite(boolean b) {
		if (b)
			return "no";
		return "yes";
	}

	public String formatBoolean(boolean b) {
		if (b)
			return ChatColor.GREEN + "YES";
		return ChatColor.RED + "NOO";
	}
	
	public String formatOppositeBoolean(boolean b) {
		if (b)
			return ChatColor.RED + "NOO";
		return ChatColor.GREEN + "YES";
	}
}