package com.massivecraft.factions;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public class TerritoryAccess {
	public String hostFactionId;
	private final boolean hostFactionAllowed;
	public Set<String> factionIds;
	private final Set<String> playerIds;

	public String getHostFactionId() {
		return this.hostFactionId;
	}

	public boolean isHostFactionAllowed() {
		return this.hostFactionAllowed;
	}

	public Set<String> getFactionIds() {
		return this.factionIds;
	}

	public Set<String> getPlayerIds() {
		return this.playerIds;
	}

	public TerritoryAccess withHostFactionId(String hostFactionId) {
		return valueOf(hostFactionId, Boolean.valueOf(this.hostFactionAllowed), this.factionIds, this.playerIds);
	}

	public TerritoryAccess withHostFactionAllowed(Boolean hostFactionAllowed) {
		return valueOf(this.hostFactionId, hostFactionAllowed, this.factionIds, this.playerIds);
	}

	public TerritoryAccess withFactionIds(Collection<String> factionIds) {
		return valueOf(this.hostFactionId, Boolean.valueOf(this.hostFactionAllowed), factionIds, this.playerIds);
	}

	public TerritoryAccess withPlayerIds(Collection<String> playerIds) {
		return valueOf(this.hostFactionId, Boolean.valueOf(this.hostFactionAllowed), this.factionIds, playerIds);
	}

	public TerritoryAccess withFactionId(String factionId, boolean with) {
		if (getHostFactionId().equals(factionId)) {
			return valueOf(this.hostFactionId, Boolean.valueOf(with), this.factionIds, this.playerIds);
		}
		Set<String> factionIds = new HashSet<String>(getFactionIds());
		if (with) {
			factionIds.add(factionId);
		} else {
			factionIds.remove(factionId);
		}
		return valueOf(this.hostFactionId, Boolean.valueOf(this.hostFactionAllowed), factionIds, this.playerIds);
	}

	public TerritoryAccess withPlayerId(String playerId, boolean with) {
		playerId = playerId.toLowerCase();
		Set<String> playerIds = new HashSet<String>(getPlayerIds());
		if (with) {
			playerIds.add(playerId);
		} else {
			playerIds.remove(playerId);
		}
		return valueOf(this.hostFactionId, Boolean.valueOf(this.hostFactionAllowed), this.factionIds, playerIds);
	}

	public TerritoryAccess toggleFactionId(String factionId) {
		return withFactionId(factionId, !isFactionIdGranted(factionId));
	}

	public TerritoryAccess togglePlayerId(String playerId) {
		return withPlayerId(playerId, !isPlayerIdGranted(playerId));
	}

	public Faction getHostFaction() {
		return Factions.getInstance().getFactionById(getHostFactionId());
	}

	public LinkedHashSet<FPlayer> getGrantedFPlayers() {
		LinkedHashSet<FPlayer> ret = new LinkedHashSet<FPlayer>();
		for (String playerId : getPlayerIds()) {
			ret.add(FPlayers.getInstance().getById(playerId));
		}
		return ret;
	}

	public LinkedHashSet<Faction> getGrantedFactions() {
		LinkedHashSet<Faction> ret = new LinkedHashSet<Faction>();
		for (String factionId : getFactionIds()) {
			ret.add(Factions.getInstance().getFactionById(factionId));
		}
		return ret;
	}

	public TerritoryAccess(String hostFactionId, Boolean hostFactionAllowed, Collection<String> factionIds,
			Collection<String> playerIds) {
		if (hostFactionId == null) {
			throw new IllegalArgumentException("hostFactionId was null");
		}
		this.hostFactionId = hostFactionId;

		Set<String> factionIdsInner = new TreeSet<String>();
		if (factionIds != null) {
			factionIdsInner.addAll(factionIds);
			if (factionIdsInner.remove(hostFactionId)) {
				hostFactionAllowed = Boolean.valueOf(true);
			}
		}
		this.factionIds = Collections.unmodifiableSet(factionIdsInner);

		Set<String> playerIdsInner = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		if (playerIds != null) {
			for (String playerId : playerIds) {
				playerIdsInner.add(playerId.toLowerCase());
			}
		}
		this.playerIds = Collections.unmodifiableSet(playerIdsInner);

		this.hostFactionAllowed = ((hostFactionAllowed == null) || (hostFactionAllowed.booleanValue()));
	}

	public static TerritoryAccess valueOf(String hostFactionId, Boolean hostFactionAllowed,
			Collection<String> factionIds, Collection<String> playerIds) {
		return new TerritoryAccess(hostFactionId, hostFactionAllowed, factionIds, playerIds);
	}

	public static TerritoryAccess valueOf(String hostFactionId) {
		return valueOf(hostFactionId, null, null, null);
	}

	public boolean isFactionIdGranted(String factionId) {
		if (getHostFactionId().equals(factionId)) {
			return isHostFactionAllowed();
		}
		return getFactionIds().contains(factionId);
	}

	public boolean isPlayerIdGranted(String playerId) {
		return getPlayerIds().contains(playerId);
	}

	public boolean isDefault() {
		return (isHostFactionAllowed()) && (getFactionIds().isEmpty()) && (getPlayerIds().isEmpty());
	}

	public Boolean hasTerritoryAccess(FPlayer fplayer) {
		if (getPlayerIds().contains(fplayer.getId())) {
			return Boolean.valueOf(true);
		}
		String factionId = fplayer.getFactionId();
		if (getFactionIds().contains(factionId)) {
			return Boolean.valueOf(true);
		}
		if ((getHostFactionId().equals(factionId)) && (!isHostFactionAllowed())) {
			return Boolean.valueOf(false);
		}
		return null;
	}
}