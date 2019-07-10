package com.massivecraft.factions.cmd;

import java.util.Collections;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.zcore.util.TL;

public class FCmdRoot extends FCommand {

	public CmdAdmin cmdAdmin = new CmdAdmin();
	public CmdAutoClaim cmdAutoClaim = new CmdAutoClaim();
	public CmdBoom cmdBoom = new CmdBoom();
	public CmdBypass cmdBypass = new CmdBypass();
	public CmdChat cmdChat = new CmdChat();
	public CmdChatSpy cmdChatSpy = new CmdChatSpy();
	public CmdClaim cmdClaim = new CmdClaim();
	public CmdConfig cmdConfig = new CmdConfig();
	public CmdCreate cmdCreate = new CmdCreate();
	public CmdDeinvite cmdDeinvite = new CmdDeinvite();
	public CmdDescription cmdDescription = new CmdDescription();
	public CmdDisband cmdDisband = new CmdDisband();
	public CmdHelp cmdHelp = new CmdHelp();
	public CmdHome cmdHome = new CmdHome();
	public CmdInvite cmdInvite = new CmdInvite();
	public CmdJoin cmdJoin = new CmdJoin();
	public CmdKick cmdKick = new CmdKick();
	public CmdLeave cmdLeave = new CmdLeave();
	public CmdList cmdList = new CmdList();
	public CmdLock cmdLock = new CmdLock();
	public CmdMap cmdMap = new CmdMap();
	public CmdMod cmdMod = new CmdMod();
	public CmdMoney cmdMoney = new CmdMoney();
	public CmdOpen cmdOpen = new CmdOpen();
	public CmdOwner cmdOwner = new CmdOwner();
	public CmdOwnerList cmdOwnerList = new CmdOwnerList();
	public CmdPeaceful cmdPeaceful = new CmdPeaceful();
	public CmdPermanent cmdPermanent = new CmdPermanent();
	public CmdPermanentPower cmdPermanentPower = new CmdPermanentPower();
	public CmdPowerBoost cmdPowerBoost = new CmdPowerBoost();
	public CmdPower cmdPower = new CmdPower();
	public CmdRelationAlly cmdRelationAlly = new CmdRelationAlly();
	public CmdRelationTruce cmdRelationTruce = new CmdRelationTruce();
	public CmdRelationEnemy cmdRelationEnemy = new CmdRelationEnemy();
	public CmdRelationNeutral cmdRelationNeutral = new CmdRelationNeutral();
	public CmdReload cmdReload = new CmdReload();
	public CmdSafeunclaimall cmdSafeunclaimall = new CmdSafeunclaimall();
	public CmdSaveAll cmdSaveAll = new CmdSaveAll();
	public CmdSethome cmdSethome = new CmdSethome();
	public CmdShow cmdShow = new CmdShow();
	public CmdStatus cmdStatus = new CmdStatus();
	public CmdStuck cmdStuck = new CmdStuck();
	public CmdTag cmdTag = new CmdTag();
	public CmdTitle cmdTitle = new CmdTitle();
	public CmdToggleAllianceChat cmdToggleAllianceChat = new CmdToggleAllianceChat();
	public CmdToggleTruceChat cmdToggleTruceChat = new CmdToggleTruceChat();
	public CmdToggleModeratorChat cmdToggleModChat = new CmdToggleModeratorChat();
	public CmdUnclaim cmdUnclaim = new CmdUnclaim();
	public CmdUnclaimall cmdUnclaimall = new CmdUnclaimall();
	public CmdVersion cmdVersion = new CmdVersion();
	public CmdWarunclaimall cmdWarunclaimall = new CmdWarunclaimall();
	public CmdShowInvites cmdShowInvites = new CmdShowInvites();
	public CmdAnnounce cmdAnnounce = new CmdAnnounce();
	public CmdSeeChunk cmdSeeChunk = new CmdSeeChunk();
	public CmdConvert cmdConvert = new CmdConvert();
	public CmdFWarp cmdFWarp = new CmdFWarp();
	public CmdSetFWarp cmdSetFWarp = new CmdSetFWarp();
	public CmdDelFWarp cmdDelFWarp = new CmdDelFWarp();
	public CmdModifyPower cmdModifyPower = new CmdModifyPower();
	public CmdLogins cmdLogins = new CmdLogins();
	public CmdClaimLine cmdClaimLine = new CmdClaimLine();
	public CmdTop cmdTop = new CmdTop();
	public CmdAHome cmdAHome = new CmdAHome();

	/**
	 * New Commands
	 */
	public CmdRules cmdRules = new CmdRules();
	public CmdRuleAdd cmdRulesAdd = new CmdRuleAdd();
	public CmdRuleRemove cmdRulesRemove = new CmdRuleRemove();
	public CmdRuleClear cmdRulesClear = new CmdRuleClear();
	public CmdOwnerRadius cmdOwnerRadius = new CmdOwnerRadius();
	public CmdCoLeader cmdColeader = new CmdCoLeader();
	public CmdPoints cmdPoints = new CmdPoints();
	public CmdCrystal cmdCrystal = new CmdCrystal();
	public CmdClaimChunk cmdClaimChunk = new CmdClaimChunk();
	public CmdShield cmdShield = new CmdShield();
	public CmdUnclaimChunk cmdUnclaimChunk = new CmdUnclaimChunk();
	public CmdAccess cmdAccess = new CmdAccess();
	public CmdPerm cmdPerm = new CmdPerm();
	public CmdCore cmdCore = new CmdCore();
	public CmdSetMaxVaults cmdSetMaxVaults = new CmdSetMaxVaults();
	public CmdVault cmdVault = new CmdVault();
	public CmdStealth cmdStealth = new CmdStealth();
	public CmdTNT cmdTNT = new CmdTNT();
	public CmdSetPoints cmdSetPoints = new CmdSetPoints();
	public CmdTokens cmdTokens = new CmdTokens();
	public CmdSetTokens cmdSetTokens = new CmdSetTokens();
	public CmdTNTChest cmdTNTChest = new CmdTNTChest();
	public CmdCalcWealth cmdCalcWealth = new CmdCalcWealth();
	public CmdStats cmdStats = new CmdStats();

	public FCmdRoot() {
		super();
		this.aliases.addAll(Conf.baseCommandAliases);
		this.aliases.removeAll(Collections.<String>singletonList(null));
		this.allowNoSlashAccess = Conf.allowNoSlashCommand;

		// this.requiredArgs.add("");
		// this.optionalArgs.put("","")

		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;

		this.disableOnLock = false;

		this.setHelpShort("The faction base command");
		this.helpLong.add(p.txt.parseTags("<i>This command contains all faction stuff."));

		// this.subCommands.add(p.cmdHelp);

		this.addSubCommand(this.cmdAdmin);
		this.addSubCommand(this.cmdAutoClaim);
		this.addSubCommand(this.cmdBoom);
		this.addSubCommand(this.cmdBypass);
		this.addSubCommand(this.cmdChat);
		this.addSubCommand(this.cmdToggleAllianceChat);
		this.addSubCommand(this.cmdToggleTruceChat);
		this.addSubCommand(this.cmdToggleModChat);
		this.addSubCommand(this.cmdChatSpy);
		this.addSubCommand(this.cmdClaim);
		this.addSubCommand(this.cmdConfig);
		this.addSubCommand(this.cmdCreate);
		this.addSubCommand(this.cmdDeinvite);
		this.addSubCommand(this.cmdDescription);
		this.addSubCommand(this.cmdDisband);
		this.addSubCommand(this.cmdHelp);
		this.addSubCommand(this.cmdHome);
		this.addSubCommand(this.cmdInvite);
		this.addSubCommand(this.cmdJoin);
		this.addSubCommand(this.cmdKick);
		this.addSubCommand(this.cmdLeave);
		this.addSubCommand(this.cmdList);
		this.addSubCommand(this.cmdLock);
		this.addSubCommand(this.cmdMap);
		this.addSubCommand(this.cmdMod);
		this.addSubCommand(this.cmdMoney);
		this.addSubCommand(this.cmdOpen);
		this.addSubCommand(this.cmdOwner);
		this.addSubCommand(this.cmdOwnerList);
		this.addSubCommand(this.cmdPeaceful);
		this.addSubCommand(this.cmdPermanent);
		this.addSubCommand(this.cmdPermanentPower);
		this.addSubCommand(this.cmdPower);
		this.addSubCommand(this.cmdPowerBoost);
		this.addSubCommand(this.cmdRelationTruce);
		this.addSubCommand(this.cmdRelationAlly);
		this.addSubCommand(this.cmdRelationEnemy);
		this.addSubCommand(this.cmdRelationNeutral);
		this.addSubCommand(this.cmdReload);
		this.addSubCommand(this.cmdSafeunclaimall);
		this.addSubCommand(this.cmdSaveAll);
		this.addSubCommand(this.cmdSethome);
		this.addSubCommand(this.cmdShow);
		this.addSubCommand(this.cmdStatus);
		this.addSubCommand(this.cmdStuck);
		this.addSubCommand(this.cmdTag);
		this.addSubCommand(this.cmdTitle);
		this.addSubCommand(this.cmdUnclaim);
		this.addSubCommand(this.cmdUnclaimall);
		this.addSubCommand(this.cmdVersion);
		this.addSubCommand(this.cmdWarunclaimall);
		this.addSubCommand(this.cmdShowInvites);
		this.addSubCommand(this.cmdAnnounce);
		this.addSubCommand(this.cmdSeeChunk);
		this.addSubCommand(this.cmdConvert);
		this.addSubCommand(this.cmdFWarp);
		this.addSubCommand(this.cmdSetFWarp);
		this.addSubCommand(this.cmdDelFWarp);
		this.addSubCommand(this.cmdModifyPower);
		this.addSubCommand(this.cmdLogins);
		this.addSubCommand(this.cmdClaimLine);
		this.addSubCommand(this.cmdTop);
		this.addSubCommand(this.cmdAHome);
		/**
		 * New Commands
		 */
		this.addSubCommand(this.cmdRules);
		this.addSubCommand(this.cmdRulesAdd);
		this.addSubCommand(this.cmdRulesClear);
		this.addSubCommand(this.cmdRulesRemove);
		this.addSubCommand(this.cmdOwnerRadius);
		this.addSubCommand(this.cmdColeader);
		this.addSubCommand(this.cmdPoints);
		this.addSubCommand(this.cmdCrystal);
		this.addSubCommand(this.cmdClaimChunk);
		this.addSubCommand(this.cmdShield);
		this.addSubCommand(this.cmdUnclaimChunk);
		this.addSubCommand(this.cmdAccess);
		this.addSubCommand(this.cmdPerm);
		this.addSubCommand(this.cmdCore);
		this.addSubCommand(this.cmdStealth);
		this.addSubCommand(this.cmdTNT);
		this.addSubCommand(this.cmdSetPoints);
		this.addSubCommand(this.cmdSetTokens);
		this.addSubCommand(this.cmdTNTChest);
		this.addSubCommand(this.cmdTokens);
		this.addSubCommand(this.cmdCalcWealth);

		if (P.p.isHookedPlayervaults()) {
			P.p.log("Found playervaults hook, adding /f vault and /f setmaxvault commands.");
			this.addSubCommand(this.cmdVault);
			this.addSubCommand(this.cmdSetMaxVaults);
		}
	}

	@Override
	public void perform() {
		this.commandChain.add(this);
		this.cmdHelp.execute(this.sender, this.args, this.commandChain);
	}

	@Override
	public TL getUsageTranslation() {
		return TL.GENERIC_PLACEHOLDER;
	}
}