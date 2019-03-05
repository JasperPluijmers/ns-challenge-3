package my_protocol;

public class ControlInformation {
    private int id;
    private int queueLength;

    public ControlInformation (int id, int queueLength) {
        this.id = id;
        this.queueLength = queueLength;
    }

    public ControlInformation (int controlInformation) {
        String controlInformationString = Integer.toString(controlInformation);

        String stringId = controlInformationString.substring(0, 5);
        String queuLength = controlInformationString.substring(5);

        this.id = Integer.parseInt(stringId, 10);
        this.queueLength = Integer.parseInt(queuLength, 10);
    }

    public int getId () {
        return  this.id;
    }

    public int getQueueLength () {
        return this.queueLength;
    }

    public int getControlInformation() {
        String idString = Integer.toString(this.id);
        String queueLengthString = Integer.toString(this.queueLength);

        return Integer.parseInt(idString + queueLengthString, 10);
    }
}