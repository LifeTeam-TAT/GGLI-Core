package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.channel.Channel;
import org.ace.insurance.system.common.channel.service.interfaces.IChannelService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "ChannelDialogActionBean")
@ViewScoped
public class ChannelDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;
	@ManagedProperty(value = "#{ChannelService}")
	private IChannelService channelService;

	public void setChannelService(IChannelService channelService) {
		this.channelService = channelService;
	}

	private List<Channel> channelList;

	@PostConstruct
	public void init() {
		channelList = channelService.findAllChannel();

	}

	public List<Channel> getChannelList() {
		return channelList;
	}

	public void setChannelList(List<Channel> channelList) {
		this.channelList = channelList;
	}

	public void selectChannel(Channel channel) {
		Channel selectChannel = channelService.findByChannelId(channel.getId());
		PrimeFaces.current().dialog().closeDynamic(selectChannel);

	}
}
