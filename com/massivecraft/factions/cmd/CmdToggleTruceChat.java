package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdToggleTruceChat extends FCommand {

    public CmdToggleTruceChat() {
        super();
        this.aliases.add("ttc");
        this.aliases.add("toggletrucechat");
        this.aliases.add("tc");

        this.disableOnLock = false;

        this.permission = Permission.TOGGLE_TRUCE_CHAT.node;
        this.disableOnLock = false;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TOGGLETRUCECHAT_DESCRIPTION;
    }

    @Override
    public void perform() {
        if (!Conf.factionOnlyChat) {
            msg(TL.COMMAND_CHAT_DISABLED.toString());
            return;
        }

        boolean ignoring = fme.isIgnoreTruceChat();

        msg(ignoring ? TL.COMMAND_TOGGLETRUCECHAT_UNIGNORE : TL.COMMAND_TOGGLETRUCECHAT_IGNORE);
        fme.setIgnoreTruceChat(!ignoring);
    }
}
