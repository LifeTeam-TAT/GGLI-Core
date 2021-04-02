/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.web.manage.system;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.channel.Channel;
import org.ace.insurance.system.common.channel.service.interfaces.IChannelService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;

@ViewScoped
@ManagedBean(name = "ManageChannelActionBean")
public class ManageChannelActionBean extends BaseBean {

	@ManagedProperty(value = "#{ChannelService}")
	private IChannelService channelService;

	public void setChannelService(IChannelService channelService) {
		this.channelService = channelService;
	}

	private boolean createNew;
	private Channel channel;
	private List<Channel> channelInfoList;
	private List<Channel> filterChannelInfoList;

	@PostConstruct
	public void init() {
		createNewChannelInfo();
		loadChannelInfo();
	}

	public void loadChannelInfo() {
		channelInfoList = channelService.findAllChannel();
	}

	public void createNewChannelInfo() {
		createNew = true;
		channel = new Channel();
		// PrimeFaces.current().resetInputs(":branchEntryForm");
	}

	public void prepareUpdateChannel(Channel channel) {
		createNew = false;
		this.channel = channel;
	}

	public void addNewChannel() {
		try {
			channelService.insert(channel);
			addInfoMessage(null, MessageId.INSERT_SUCCESS, channel.getName());
			createNewChannelInfo();
			loadChannelInfo();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadChannelInfo();
	}

	public void updateChannel() {
		try {
			channelService.update(channel);
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, channel.getName());
			createNewChannelInfo();
			loadChannelInfo();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadChannelInfo();
	}

	public void deleteChannel(Channel channel) {
		try {
			channelService.delete(channel);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, channel.getName());
			createNewChannelInfo();
			loadChannelInfo();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		loadChannelInfo();
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public List<Channel> getChannelInfoList() {
		return channelInfoList;
	}

	public void setChannelInfoList(List<Channel> channelInfoList) {
		this.channelInfoList = channelInfoList;
	}

	public List<Channel> getFilterChannelInfoList() {
		return filterChannelInfoList;
	}

	public void setFilterChannelInfoList(List<Channel> filterChannelInfoList) {
		this.filterChannelInfoList = filterChannelInfoList;
	}

	public void setCreateNew(boolean createNew) {
		this.createNew = createNew;
	}

}
