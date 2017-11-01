package com.fiscalleti.recipecreator;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.google.common.collect.Lists;

public class ChatOutput {
	private final List<CommandSender> outputs;

	private ChatOutput(final List<CommandSender> subs) {
		this.outputs = subs;
	}

	public static ChatOutput create(final CommandSender... outputs) {
		return new ChatOutput(Lists.newArrayList(outputs));
	}

	public void sendMessage(final String arg0) {
		for (final CommandSender sub : this.outputs)
			sub.sendMessage(arg0);
	}

	public void sendMessage(final String[] arg0) {
		for (final CommandSender sub : this.outputs)
			sub.sendMessage(arg0);
	}
}
