package tcpFsm;
public class EventImp extends Fsm.Event {
    public EventImp(String event){
        super(event);
    }
    public EventImp(String event, int obj){
        super(event, obj);
    }
}
