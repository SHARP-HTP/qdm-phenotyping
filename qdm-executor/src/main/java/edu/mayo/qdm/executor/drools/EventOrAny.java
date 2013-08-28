package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Event;

/**
 */
public class EventOrAny {

    private Event event;
    private boolean any;

    public EventOrAny(){
        super();
        this.any = true;
    }

    public EventOrAny(Event event){
        super();
        this.any = false;
        this.event = event;
    }

    public EventOrAny intersect(EventOrAny eventOrAny){
        if(this.any && eventOrAny.any){
            return new EventOrAny();
        } else if(this.event != null || eventOrAny.event != null){
            if(this.event != null && eventOrAny.event != null){
                if(this.event == eventOrAny.event){
                    return new EventOrAny(this.event);
                }
            } else {
                if(this.event != null){
                    return new EventOrAny(this.event);
                } else {
                    return new EventOrAny(eventOrAny.event);
                }
            }
        }

        return null;
    }

    public Event getEvent() {
        return event;
    }

    public boolean isAny() {
        return any;
    }

    @Override
    public String toString() {
        return "EventOrAny{" +
                "event=" + event +
                ", any=" + any +
                '}';
    }
}
