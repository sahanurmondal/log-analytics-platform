package miscellaneous.zoom;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Custom Question: Design Video Conferencing System
 * 
 * Description:
 * Design a video conferencing platform that supports:
 * - Meeting creation and management
 * - Real-time video/audio streaming
 * - Screen sharing
 * - Recording and playback
 * - Participant management
 * 
 * Company: Zoom
 * Difficulty: Hard
 * Asked: System design interviews 2023-2024
 */
public class DesignVideoConferencing {

    enum MeetingStatus {
        SCHEDULED, LIVE, ENDED
    }

    enum ParticipantStatus {
        JOINED, LEFT, MUTED, UNMUTED
    }

    class Meeting {
        String id;
        String title;
        String hostId;
        long scheduledTime;
        long duration;
        MeetingStatus status;
        Set<String> participants;
        boolean isRecording;
        String recordingUrl;

        Meeting(String title, String hostId, long scheduledTime, long duration) {
            this.id = UUID.randomUUID().toString();
            this.title = title;
            this.hostId = hostId;
            this.scheduledTime = scheduledTime;
            this.duration = duration;
            this.status = MeetingStatus.SCHEDULED;
            this.participants = new HashSet<>();
            this.isRecording = false;
        }
    }

    class Participant {
        String userId;
        String meetingId;
        ParticipantStatus status;
        boolean videoEnabled;
        boolean audioEnabled;
        boolean isScreenSharing;
        long joinTime;

        Participant(String userId, String meetingId) {
            this.userId = userId;
            this.meetingId = meetingId;
            this.status = ParticipantStatus.JOINED;
            this.videoEnabled = true;
            this.audioEnabled = true;
            this.isScreenSharing = false;
            this.joinTime = System.currentTimeMillis();
        }
    }

    class StreamingEngine {
        public void startVideoStream(String userId, String meetingId) {
            String streamId = userId + "_" + meetingId + "_video";
            activeStreams.put(streamId, new VideoStream(streamId, userId, meetingId));

            // Broadcast to other participants
            broadcastToParticipants(meetingId, "video_started", userId);
        }

        public void startAudioStream(String userId, String meetingId) {
            String streamId = userId + "_" + meetingId + "_audio";
            activeStreams.put(streamId, new AudioStream(streamId, userId, meetingId));

            // Broadcast to other participants
            broadcastToParticipants(meetingId, "audio_started", userId);
        }

        public void startScreenShare(String userId, String meetingId) {
            Participant participant = getParticipant(userId, meetingId);
            if (participant != null) {
                participant.isScreenSharing = true;
                broadcastToParticipants(meetingId, "screen_share_started", userId);
            }
        }

        public void broadcastToParticipants(String meetingId, String event, String userId) {
            Meeting meeting = meetings.get(meetingId);
            if (meeting != null) {
                for (String participantId : meeting.participants) {
                    if (!participantId.equals(userId)) {
                        sendNotification(participantId, event, userId);
                    }
                }
            }
        }

        private void sendNotification(String userId, String event, String sourceUserId) {
            System.out.println("Notifying " + userId + " about " + event + " from " + sourceUserId);
        }
    }

    class RecordingManager {
        public void startRecording(String meetingId) {
            Meeting meeting = meetings.get(meetingId);
            if (meeting != null && meeting.status == MeetingStatus.LIVE) {
                meeting.isRecording = true;
                meeting.recordingUrl = generateRecordingUrl(meetingId);

                // Notify participants
                streamingEngine.broadcastToParticipants(meetingId, "recording_started", meeting.hostId);
            }
        }

        public void stopRecording(String meetingId) {
            Meeting meeting = meetings.get(meetingId);
            if (meeting != null && meeting.isRecording) {
                meeting.isRecording = false;

                // Process and save recording
                processRecording(meeting.recordingUrl);

                // Notify participants
                streamingEngine.broadcastToParticipants(meetingId, "recording_stopped", meeting.hostId);
            }
        }

        private String generateRecordingUrl(String meetingId) {
            return "https://recordings.zoom.com/" + meetingId + "_" + System.currentTimeMillis();
        }

        private void processRecording(String recordingUrl) {
            // Process and compress recording
            System.out.println("Processing recording: " + recordingUrl);
        }
    }

    class VideoStream {
        String streamId;
        String userId;
        String meetingId;

        VideoStream(String streamId, String userId, String meetingId) {
            this.streamId = streamId;
            this.userId = userId;
            this.meetingId = meetingId;
        }
    }

    class AudioStream {
        String streamId;
        String userId;
        String meetingId;

        AudioStream(String streamId, String userId, String meetingId) {
            this.streamId = streamId;
            this.userId = userId;
            this.meetingId = meetingId;
        }
    }

    private Map<String, Meeting> meetings = new HashMap<>();
    private Map<String, Participant> participants = new HashMap<>();
    private Map<String, Object> activeStreams = new HashMap<>();
    private StreamingEngine streamingEngine = new StreamingEngine();
    private RecordingManager recordingManager = new RecordingManager();

    public String scheduleMeeting(String title, String hostId, long scheduledTime, long duration) {
        Meeting meeting = new Meeting(title, hostId, scheduledTime, duration);
        meetings.put(meeting.id, meeting);
        return meeting.id;
    }

    public boolean joinMeeting(String userId, String meetingId) {
        Meeting meeting = meetings.get(meetingId);
        if (meeting == null)
            return false;

        if (meeting.status == MeetingStatus.SCHEDULED) {
            meeting.status = MeetingStatus.LIVE;
        }

        meeting.participants.add(userId);
        Participant participant = new Participant(userId, meetingId);
        participants.put(userId + "_" + meetingId, participant);

        // Start streams
        streamingEngine.startVideoStream(userId, meetingId);
        streamingEngine.startAudioStream(userId, meetingId);

        return true;
    }

    public void leaveMeeting(String userId, String meetingId) {
        Meeting meeting = meetings.get(meetingId);
        if (meeting != null) {
            meeting.participants.remove(userId);

            Participant participant = participants.get(userId + "_" + meetingId);
            if (participant != null) {
                participant.status = ParticipantStatus.LEFT;
            }

            // If host leaves, end meeting
            if (userId.equals(meeting.hostId)) {
                endMeeting(meetingId);
            }
        }
    }

    public void endMeeting(String meetingId) {
        Meeting meeting = meetings.get(meetingId);
        if (meeting != null) {
            meeting.status = MeetingStatus.ENDED;

            // Stop recording if active
            if (meeting.isRecording) {
                recordingManager.stopRecording(meetingId);
            }

            // Notify all participants
            streamingEngine.broadcastToParticipants(meetingId, "meeting_ended", meeting.hostId);
        }
    }

    public void startScreenShare(String userId, String meetingId) {
        streamingEngine.startScreenShare(userId, meetingId);
    }

    public void startRecording(String meetingId) {
        recordingManager.startRecording(meetingId);
    }

    public void muteParticipant(String userId, String meetingId) {
        Participant participant = getParticipant(userId, meetingId);
        if (participant != null) {
            participant.audioEnabled = false;
            participant.status = ParticipantStatus.MUTED;
            streamingEngine.broadcastToParticipants(meetingId, "participant_muted", userId);
        }
    }

    public void unmuteParticipant(String userId, String meetingId) {
        Participant participant = getParticipant(userId, meetingId);
        if (participant != null) {
            participant.audioEnabled = true;
            participant.status = ParticipantStatus.UNMUTED;
            streamingEngine.broadcastToParticipants(meetingId, "participant_unmuted", userId);
        }
    }

    public void toggleVideo(String userId, String meetingId) {
        Participant participant = getParticipant(userId, meetingId);
        if (participant != null) {
            participant.videoEnabled = !participant.videoEnabled;
            String event = participant.videoEnabled ? "video_enabled" : "video_disabled";
            streamingEngine.broadcastToParticipants(meetingId, event, userId);
        }
    }

    public List<Participant> getMeetingParticipants(String meetingId) {
        Meeting meeting = meetings.get(meetingId);
        if (meeting == null)
            return new ArrayList<>();

        return meeting.participants.stream()
                .map(participantId -> getParticipant(participantId, meetingId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Meeting getMeetingInfo(String meetingId) {
        return meetings.get(meetingId);
    }

    private Participant getParticipant(String userId, String meetingId) {
        return participants.get(userId + "_" + meetingId);
    }

    public static void main(String[] args) {
        DesignVideoConferencing zoom = new DesignVideoConferencing();

        // Schedule meeting
        String meetingId = zoom.scheduleMeeting("Team Standup", "host1",
                System.currentTimeMillis() + 3600000, 3600000);

        // Join meeting
        zoom.joinMeeting("host1", meetingId);
        zoom.joinMeeting("user1", meetingId);

        // Start screen share
        zoom.startScreenShare("host1", meetingId);

        // Start recording
        zoom.startRecording(meetingId);

        // Mute participant
        zoom.muteParticipant("user1", meetingId);

        // Toggle video
        zoom.toggleVideo("user1", meetingId);

        // Get meeting info
        Meeting meeting = zoom.getMeetingInfo(meetingId);
        System.out.println("Meeting: " + meeting.title + " - Status: " + meeting.status);

        // Get participants
        List<Participant> participants = zoom.getMeetingParticipants(meetingId);
        System.out.println("Participants: " + participants.size());

        System.out.println("Video conference started: " + meetingId);
    }
}
