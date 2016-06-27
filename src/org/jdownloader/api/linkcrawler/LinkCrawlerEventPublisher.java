package org.jdownloader.api.linkcrawler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import jd.controlling.linkcollector.LinkCollectingJob;
import jd.controlling.linkcollector.LinkCollector.JobLinkCrawler;
import jd.controlling.linkcrawler.CrawledLink;
import jd.controlling.linkcrawler.CrawledPackage;
import jd.controlling.linkcrawler.LinkCrawler;
import jd.controlling.linkcrawler.LinkCrawlerEvent;
import jd.controlling.linkcrawler.LinkCrawlerListener;

import org.appwork.remoteapi.events.EventPublisher;
import org.appwork.remoteapi.events.RemoteAPIEventsSender;
import org.appwork.remoteapi.events.SimpleEventObject;

public class LinkCrawlerEventPublisher implements EventPublisher, LinkCrawlerListener {

    private final CopyOnWriteArraySet<RemoteAPIEventsSender> eventSenders = new CopyOnWriteArraySet<RemoteAPIEventsSender>();
    private final String[]                                   eventIDs;

    private enum EVENTID {
        STARTED,
        STOPPED,
        FINISHED
    }

    public LinkCrawlerEventPublisher() {
        eventIDs = new String[] { EVENTID.STARTED.name(), EVENTID.STOPPED.name(), EVENTID.FINISHED.name() };
    }

    @Override
    public void onLinkCrawlerEvent(LinkCrawlerEvent event) {
        if (!eventSenders.isEmpty()) {
            HashMap<String, Object> dls = new HashMap<String, Object>();
            final LinkCrawler crawler = event.getCaller();
            dls.put("crawlerId", crawler.getUniqueAlltimeID().getID());
            if (event.getCaller() instanceof JobLinkCrawler) {
                JobLinkCrawler jobCrawler = ((JobLinkCrawler) crawler);
                final LinkCollectingJob job = ((JobLinkCrawler) crawler).getJob();
                if (job != null) {
                    dls.put("jobId", job.getUniqueAlltimeID().getID());
                    if (LinkCrawlerEvent.Type.STOPPED.equals(event.getType())) {
                        final List<CrawledLink> linklist = jobCrawler.getCrawledLinks();
                        final HashSet<CrawledPackage> dupe = new HashSet<CrawledPackage>();
                        int offlineCnt = 0;
                        int onlineCnt = 0;
                        synchronized (linklist) {
                            for (final CrawledLink cl : linklist) {
                                dupe.add(cl.getParentNode());
                                switch (cl.getLinkState()) {
                                case OFFLINE:
                                    offlineCnt++;
                                    break;
                                case ONLINE:
                                    onlineCnt++;
                                    break;
                                }
                            }
                        }

                        dls.put("packages", dupe.size());
                        dls.put("links", jobCrawler.getCrawledLinksFoundCounter());
                        dls.put("online", onlineCnt);
                        dls.put("offline", offlineCnt);
                    }
                }

            }
            final SimpleEventObject eventObject = new SimpleEventObject(this, event.getType().name(), dls);
            // you can add uniqueID of linkcrawler and job to event
            for (RemoteAPIEventsSender eventSender : eventSenders) {
                eventSender.publishEvent(eventObject, null);
            }

        }
    }

    @Override
    public String[] getPublisherEventIDs() {
        return eventIDs;
    }

    @Override
    public String getPublisherName() {
        return "linkcrawler";
    }

    @Override
    public void register(RemoteAPIEventsSender eventsAPI) {
        final boolean wasEmpty = eventSenders.isEmpty();
        eventSenders.add(eventsAPI);
        if (wasEmpty && eventSenders.isEmpty() == false) {
            LinkCrawler.getGlobalEventSender().addListener(this, true);
        }
    }

    @Override
    public void unregister(RemoteAPIEventsSender eventsAPI) {
        eventSenders.remove(eventsAPI);
        if (eventSenders.isEmpty()) {
            LinkCrawler.getGlobalEventSender().removeListener(this);
        }
    }

}
