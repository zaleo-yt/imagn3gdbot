package com.github.alex1304.ultimategdbot.modules.gdevents.broadcast;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IRole;

/**
 * Builds a message with an optional role to tag at the beginning of the message
 *
 * @author Alex1304
 */
public class OptionalRoleTagMessage implements BroadcastableMessage {

	private String baseContent;
	private String privateContent;
	private EmbedObject baseEmbed;
	private IRole roleToPing;
	
	public OptionalRoleTagMessage() {
	}
	
	public OptionalRoleTagMessage(String baseContent, String privateContent, EmbedObject baseEmbed, IRole roleToPing) {
		this.baseContent = baseContent;
		this.privateContent = privateContent;
		this.baseEmbed = baseEmbed;
		this.roleToPing = roleToPing;
	}

	@Override
	public String buildContent() {
		StringBuffer sb = new StringBuffer();
		
		if (roleToPing != null)
			sb.append(roleToPing.mention() + " ");
		
		return sb.append(baseContent).toString();
	}

	@Override
	public EmbedObject buildEmbed() {
		return baseEmbed;
	}

	/**
	 * Gets the baseContent
	 *
	 * @return String
	 */
	public String getBaseContent() {
		return baseContent;
	}

	/**
	 * Gets the privateContent
	 *
	 * @return String
	 */
	public String getPrivateContent() {
		return privateContent;
	}

	/**
	 * Gets the baseEmbed
	 *
	 * @return EmbedObject
	 */
	public EmbedObject getBaseEmbed() {
		return baseEmbed;
	}

	/**
	 * Gets the roleToPing
	 *
	 * @return IRole
	 */
	public IRole getRoleToPing() {
		return roleToPing;
	}

	/**
	 * Sets the baseContent
	 *
	 * @param baseContent - String
	 */
	public void setBaseContent(String baseContent) {
		this.baseContent = baseContent;
	}

	/**
	 * Sets the privateContent
	 *
	 * @param privateContent - String
	 */
	public void setPrivateContent(String privateContent) {
		this.privateContent = privateContent;
	}

	/**
	 * Sets the baseEmbed
	 *
	 * @param baseEmbed - EmbedObject
	 */
	public void setBaseEmbed(EmbedObject baseEmbed) {
		this.baseEmbed = baseEmbed;
	}

	/**
	 * Sets the roleToPing
	 *
	 * @param roleToPing - IRole
	 */
	public void setRoleToPing(IRole roleToPing) {
		this.roleToPing = roleToPing;
	}
	
	@Override
	public String toString() {
		return "OptionalRoleTagMessage [baseContent=" + baseContent + ", baseEmbed=" + baseEmbed + ", roleToPing="
				+ roleToPing + "]";
	}

}
