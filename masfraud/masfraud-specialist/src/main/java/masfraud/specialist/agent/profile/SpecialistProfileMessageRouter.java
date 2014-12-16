package masfraud.specialist.agent.profile;

import com.google.gson.Gson;
import masfraud.base.BaseAgent;
import masfraud.base.command.PrintMessageCommand;
import masfraud.base.command.ReplicationCommand;
import masfraud.base.constants.ContextKey;
import masfraud.base.constants.EventActionType;
import masfraud.base.constants.EventType;
import masfraud.base.constants.ServiceName;
import masfraud.base.message.BaseEvent;
import masfraud.base.message.ReplicaEvent;
import masfraud.base.router.BaseMessageRouter;
import masfraud.specialist.agent.ProcessorEngine;
import masfraud.specialist.agent.base.command.CommandBase;
import masfraud.specialist.agent.profile.command.R01ProfileCommand;
import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.CatalogBase;
import org.apache.log4j.Logger;

/**
 * 
 * @author Mauricio
 * 
 */
public class SpecialistProfileMessageRouter extends BaseMessageRouter {

	private static final long serialVersionUID = -3782124025021376578L;

	private static Logger LOG = Logger.getLogger(SpecialistProfileMessageRouter.class);
	
	private Catalog catalog = new CatalogBase();
	
	public SpecialistProfileMessageRouter() {
		createValidationCatalog();
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Boolean routeMessage(Context context) {
		LOG.info(this.getClass().getName() + ": " + "Method: routeMessage");

		BaseEvent originalMessage = (BaseEvent) context.get(ContextKey.INCOMING_MESSAGE.getValue());
		BaseAgent agent = (BaseAgent)context.get(ContextKey.AGENT_INSTANCE.getValue());

		switch (originalMessage.getType()) {

		case REPLICA:
			String strMessage = (String)context.get(ContextKey.STRING_MESSAGE.getValue());
			ReplicaEvent replicaMessage = new Gson().fromJson(strMessage, ReplicaEvent.class);
			
			context.put(ContextKey.MESSAGE.getValue(), replicaMessage);
			try {
				//new PrintMessageCommand().execute(context);
				System.out.println("Agent " + agent.getLocalName() + " replica command...");
				new ReplicationCommand().execute(context);
			} catch (Exception e) {
				System.err.println(e);
				return false;
			}
			break;

		case EVENT:
			try {
				System.out.println("SpecialistMessageRouter: routeMessage -> EVENT");


                CommandBase commandBase = new CommandBase();
                commandBase.setEventActionType(EventActionType.INSERT_MEMBER_DELIVERY_ADDRESS);
                commandBase.setEventType(EventType.PROFILE);
                commandBase.execute(context);

                /*//Chama o motor para poder executar as regras {Gerar alerta ou nao}
                ProcessorEngine.getInstance().engine(context, ServiceName.PROFILE);*/

				new PrintMessageCommand().execute(context);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		default:
			break;

		}

		return null;
	}



    private void executeValidation(){
		
	}
	
	
	
	/**
	 * 
	 */
	private void createValidationCatalog(){
		catalog.addCommand("R01", new R01ProfileCommand());
	}
	
	
	
	
}
