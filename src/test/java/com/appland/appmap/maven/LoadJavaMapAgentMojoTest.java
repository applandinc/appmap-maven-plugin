package com.appland.appmap.maven;

import static org.junit.Assert.assertEquals;

import com.appland.appmap.output.v1.Event;

import com.appland.appmap.record.RecordingSession;
import com.appland.appmap.record.Recorder;
import com.appland.appmap.record.Recording;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

public class LoadJavaMapAgentMojoTest {
    @Before
    public void before() throws Exception {
        final Recorder.Metadata metadata = new Recorder.Metadata();
        Recorder.getInstance().start(metadata);
    }

    @Test
    public void testAllEventsWritten() throws IOException {
        final Recorder recorder = Recorder.getInstance();
        final Long threadId = Thread.currentThread().getId();
        final Event[] events = new Event[] {
                new Event(),
                new Event(),
                new Event(),
        };

        for (int i = 0; i < events.length; i++) {
            final Event event = events[i];
            event
                    .setDefinedClass("SomeClass")
                    .setEvent("call")
                    .setMethodId("SomeMethod")
                    .setStatic(false)
                    .setLineNumber(315)
                    .setThreadId(threadId);

            System.err.println(recorder);
            System.err.println(event);
            recorder.add(event);
            assertEquals(event, recorder.getLastEvent());
        }

        final Recording recording = recorder.stop();
        StringWriter appmapWriter = new StringWriter();
        recording.readFully(true, appmapWriter);
        String appmapJson = appmapWriter.toString();

        final String expectedJson = "\"thread_id\":" + threadId.toString();
        final int numMatches = StringUtils.countMatches(appmapJson, expectedJson);
        assertEquals(numMatches, events.length);
    }
}
