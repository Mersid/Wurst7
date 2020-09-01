package net.wurstclient.events;

import net.wurstclient.event.Event;
import net.wurstclient.event.Listener;

import java.util.ArrayList;

public interface PartialRenderListener extends Listener {
    void onPartialRender(float partialTicks);

    class PartialRenderEvent extends Event<PartialRenderListener>
    {
        private final float partialTicks;

        public PartialRenderEvent(float partialTicks)
        {
            this.partialTicks = partialTicks;
        }

        @Override
        public void fire(ArrayList<PartialRenderListener> listeners)
        {
            for(PartialRenderListener listener : listeners)
                listener.onPartialRender(partialTicks);
        }

        @Override
        public Class<PartialRenderListener> getListenerType()
        {
            return PartialRenderListener.class;
        }
    }
}
