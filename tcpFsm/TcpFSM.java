package tcpFsm;

import Fsm.FSM;
import Fsm.FsmException;
import Fsm.Transition;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TcpFSM {
    static FSM tcp;
    static ActionImp act = new ActionImp();
    public static HashMap<String, StateImp> states = new HashMap<String, StateImp>();
    public static HashMap<String, EventImp> events = new HashMap<String, EventImp>();

    public static void main(String[] args) {
        createStates();
        createEvents();
        tcp = new FSM("TCP", states.get("LISTEN"));
        createTransitions();
        Scanner sc = new Scanner(System.in);
        while(sc.hasNextLine()){
            String str = sc.nextLine();
            if(str.isEmpty()) return;
            if(!events.containsKey(str)) {
                System.out.println("Error: unexpected Event: " + str);
            }
            else {
                try {
                    tcp.doEvent(events.get(str));
                } catch (FsmException e) {
                    System.out.println("Error: unexpected Event: " + str);
                }
            }

        }
    }

    private static void createTransitions(){
        for(Map.Entry<String, StateImp> st: states.entrySet()) {
            String cState = st.getKey();
            switch (cState) {
                case "CLOSED":
                    createAndAddTran(cState, "PASSIVE", "LISTEN");
                    createAndAddTran(cState, "ACTIVE", "SYN_SENT");
                    break;
                case "LISTEN":
                    createAndAddTran(cState, "CLOSE", "CLOSED");
                    createAndAddTran(cState, "SEND", "SYN_SENT");
                    createAndAddTran(cState, "SYN", "SYN_RCVD");
                    break;
                case "SYN_RCVD":
                    createAndAddTran(cState, "ACK", "ESTABLISHED");
                    createAndAddTran(cState, "CLOSE", "FIN_WAIT_1");
                    break;
                case "SYN_SENT":
                    createAndAddTran(cState, "SYN", "SYN_RCVD");
                    createAndAddTran(cState, "SYNACK", "ESTABLISHED");
                    createAndAddTran(cState, "CLOSE", "CLOSED");
                    break;
                case "ESTABLISHED":
                    createAndAddTran(cState, "SDATA", "ESTABLISHED");
                    createAndAddTran(cState, "RDATA", "ESTABLISHED");
                    createAndAddTran(cState, "FIN", "CLOSE_WAIT");
                    createAndAddTran(cState, "CLOSE", "FIN_WAIT_1");
                    break;
                case "FIN_WAIT_1":
                    createAndAddTran(cState, "ACK", "FIN_WAIT_2");
                    createAndAddTran(cState, "FIN", "CLOSING");
                    break;
                case "FIN_WAIT_2":
                    createAndAddTran(cState, "FIN", "TIME_WAIT");
                    break;
                case "CLOSING":
                    createAndAddTran(cState, "ACK", "TIME_WAIT");
                    break;
                case "TIME_WAIT":
                    createAndAddTran(cState, "TIMEOUT", "CLOSED");
                    break;
                case "CLOSE_WAIT":
                    createAndAddTran(cState, "CLOSE", "LAST_ACK");
                    break;
                case "LAST_ACK":
                    createAndAddTran(cState, "ACK", "CLOSED");
                    break;
            }
        }
    }
    private static void createAndAddTran(String cState, String event, String nState){
        try {
            Transition t = new Transition(states.get(cState), events.get(event), states.get(nState), act);
            tcp.addTransition(t);
        } catch (FsmException e) {}
    }
    private static void createStates() {
        String[] stateKeys = new String[]{"CLOSED", "LISTEN", "SYN_RCVD", "SYN_SENT", "ESTABLISHED",
                "FIN_WAIT_1", "FIN_WAIT_2", "CLOSING", "TIME_WAIT", "CLOSE_WAIT", "LAST_ACK"};
        for(String state: stateKeys) {
            states.put(state, new StateImp(state));
        }
    }

    private static void createEvents() {
        String[] eventKeys = new String[]{"PASSIVE", "ACTIVE", "SYN", "SYNACK", "ACK", "RDATA", "SDATA",
                "FIN", "CLOSE", "TIMEOUT", "SEND"};
        for(String event: eventKeys) {
            events.put(event, new EventImp(event));
        }
        events.get("SDATA").setValue(0);
        events.get("RDATA").setValue(0);
    }
}
