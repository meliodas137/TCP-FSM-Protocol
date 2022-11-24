package tcpFsm;

import Fsm.Event;
import Fsm.FSM;
import Fsm.Action;

public class ActionImp extends Action {
    public ActionImp() {
        super();
    }

    @Override
    public void execute(FSM fsm, Event event) {
        String eventName = event.getName();
        if(eventName.compareTo("RDATA") == 0){
            event.setValue((int)event.getValue()+1);
            System.out.println("DATA received " + event.getValue());
        }
        else if(eventName.compareTo("SDATA") == 0){
            event.setValue((int)event.getValue()+1);
            System.out.println("DATA sent " + event.getValue());
        }
        else {
            System.out.println("Event " + event.getName() + " received, current State is " + fsm.currentState().getName());
        }
    }

}
