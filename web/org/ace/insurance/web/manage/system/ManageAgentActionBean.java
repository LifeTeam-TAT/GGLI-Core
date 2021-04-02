/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 * 
 ***************************************************************************************/
package org.ace.insurance.web.manage.system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.ace.insurance.common.AgentCriteria;
import org.ace.insurance.system.common.agent.AGT001;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.agent.service.interfaces.IAgentService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;

@ViewScoped
@ManagedBean(name = "ManageAgentActionBean")
public class ManageAgentActionBean extends BaseBean implements Serializable {
  private static final long serialVersionUID = 1L;

  @ManagedProperty(value = "#{AgentService}")
  private IAgentService agentService;

  public void setAgentService(IAgentService agentService) {
    this.agentService = agentService;
  }

  private AgentCriteria agentCriteria;
  private List<AGT001> agentList;

  @PostConstruct
  public void init() {
    resetAgent();
  }

  public void resetAgent() {
    agentCriteria = new AgentCriteria();
    agentList = new ArrayList<>();
  }

  public void searchAgent() {
    agentList = agentService.findAgentByCriteria(agentCriteria, 30);
  }

  public void outjectAgent(Agent agent) {
    putParam("agent", agent);

  }

  public String updateAgent(AGT001 agt001) {
    String result = null;
    try {
      Agent agent = agentService.findAgentById(agt001.getId());
      outjectAgent(agent);
      result = "addNewAgent";

    } catch (SystemException ex) {
      handelSysException(ex);
    }

    return result;
  }

  public String creatNewAgent() {
    return "addNewAgent";
  }

  public void deleteAgent(AGT001 agt001) {
    try {
      agentService.CreateHistoryAndRemoveAgentById(agt001.getId());
      agentList.remove(agt001);
      addInfoMessage(null, MessageId.DELETE_SUCCESS, agt001.getFullName());
    } catch (SystemException ex) {
      handelSysException(ex);
    }
  }

  public AgentCriteria getAgentCriteria() {
    return agentCriteria;
  }

  public List<AGT001> getAgentList() {
    return agentList;
  }

}
