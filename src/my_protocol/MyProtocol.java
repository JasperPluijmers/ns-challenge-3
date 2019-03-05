package my_protocol;

import framework.IMACProtocol;
import framework.MediumState;
import framework.TransmissionInfo;
import framework.TransmissionType;

import java.util.*;

public class MyProtocol implements IMACProtocol {
    private static final int idUpper = 99999;
    private static final int idLower = 10000;
    private static final int MAXQUEUE = 200;
    private int id;
    private TreeSet<Integer> ids;
    private int lastSeen;
    private int lastFlag;

    public MyProtocol() {
        this.id = new Random().nextInt(idUpper - idLower) + idLower;
        ids = new TreeSet<>();
        lastFlag = 0;
    }

    public int nextId(int currentId) {
        if (ids.isEmpty()) {
            return 0;
        }
        if (ids.higher(currentId) == null) {
            return ids.first();
        }
        return ids.higher(currentId);
    }


    public TransmissionInfo sendData(int localQueueLength, int queuelength) {
        if (localQueueLength != 0) {
            return new TransmissionInfo(TransmissionType.Data, new ControlInformation(id, queuelength).getControlInformation());
        }
        return new TransmissionInfo(TransmissionType.Silent, 0);
    }


    private boolean collision = false;
    public TransmissionInfo transmittingBehaviour(MediumState previousMediumState,
                                                  int controlInformation, int localQueueLength) {
        if (lastFlag == 0) {
            if (previousMediumState == MediumState.Idle && !collision) {
                ids.remove(nextId(lastSeen));

            }
            if (localQueueLength == 0) {
                return new TransmissionInfo(TransmissionType.Silent, 0);
            }

            if (!ids.contains(id)) {
                if (new Random().nextInt(100) > 10) {
                    return sendData(localQueueLength, localQueueLength - 1);
                }
                return new TransmissionInfo(TransmissionType.Silent, 0);
            }

            if (previousMediumState == MediumState.Collision || nextId(lastSeen) != id) {
                return new TransmissionInfo(TransmissionType.Silent, 0);
            }
            return sendData(localQueueLength, localQueueLength - 1);
        } else if (lastSeen == id) {
            sendData(localQueueLength,Math.min(lastFlag-1, localQueueLength - 1));
        }
        return new TransmissionInfo(TransmissionType.Silent, 0);
    }

    @Override
    public TransmissionInfo TimeslotAvailable(MediumState previousMediumState,
                                              int controlInformation, int localQueueLength) {
/*
        System.out.println("COntrolinformationreceived: " + controlInformation);
        System.out.println("previousmediumState: " + previousMediumState);
        System.out.println("localQueueLength: " + localQueueLength);
        System.out.println("queuueueue: " + ids);
*/

        System.out.println(previousMediumState);

        if (previousMediumState != MediumState.Idle && previousMediumState != MediumState.Collision) {
            lastSeen = new ControlInformation(controlInformation).getId();
            lastFlag = new ControlInformation(controlInformation).getQueueLength();
            ids.add(lastSeen);
        }
        if (previousMediumState == MediumState.Collision) {
            collision = true;
        } else {
            collision = false;
        }
        lastFlag = 0;
        return transmittingBehaviour(previousMediumState, controlInformation, localQueueLength);
    }
}