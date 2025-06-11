import java.util.Random;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
            public static Scanner sc = new Scanner(System.in);
            public static Random rand = new Random();
            public static String[] cabinsA = {
                    "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
                    "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
            };
            public static String[] cabinsB = {
                    "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
                    "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
            };
            public static boolean[] isBusyA = new boolean[26];
            public static boolean[] isBusyB = new boolean[26];
            public static String[] activeCodes = new String[100];
            public static String[] activeCaller = new String[100];
            public static String[] activeReceiver = new String[100];
            public static String[] activeType = new String[100];
            public static int activeCallsCount = 0;
            public static String[] pausedCodesA = new String[130];
            public static String[] pausedCallersA = new String[130];
            public static String[] pausedReceiversA = new String[130];
            public static int[] pausedCountA = new int[26];
            public static String[] pausedCodesB = new String[130];
            public static String[] pausedCallersB = new String[130];
            public static String[] pausedReceiversB = new String[130];
            public static int[] pausedCountB = new int[26];
            public static String[] conferenceParticipants = new String[105];
            public static boolean[] serverBusy = new boolean[5];
            public static String[] conferenceCodes = new String[5];
            public static int[] serverParticipantCount = new int[5];
            public static void main(String[] args) {
                for (int i = 0; i < 26; i++) {
                    isBusyA[i] = false;
                    isBusyB[i] = false;
                    pausedCountA[i] = 0;
                    pausedCountB[i] = 0;
                }
                for (int i = 0; i < 5; i++) {
                    serverBusy[i] = false;
                    serverParticipantCount[i] = 0;
                }
                System.out.println("=== ТЕЛЕФОННА ЦЕНТРАЛА ===");
                System.out.println("Централа A: A-Z (26 кабинки)");
                System.out.println("Централа B: a-z (26 кабинки)");
                while (true) {
                    showMenu();
                    String command = sc.nextLine().trim();
                    if (command.equals("1") || command.equals("dial")) {
                        dial();
                    } else if (command.equals("2") || command.equals("conference")) {
                        conference();
                    } else if (command.equals("3") || command.equals("show")) {
                        showAllCalls();
                    } else if (command.equals("4") || command.equals("end")) {
                        endCall();
                    } else if (command.equals("5") || command.equals("resume")) {
                        resumeCall();
                    } else if (command.equals("6") || command.equals("status")) {
                        showSystemStatus();
                    } else if (command.equals("7") || command.equals("exit")) {
                        System.out.println("Довиждане!");
                        break;
                    } else {
                        System.out.println("Невалидна команда!");
                    }
                }
            }

            public static void showMenu() {
                System.out.println("\n--- КОМАНДИ ---");
                System.out.println("1. dial - Обади се");
                System.out.println("2. conference - Конферентен разговор");
                System.out.println("3. show - Покажи активни разговори");
                System.out.println("4. end - Прекрати разговор");
                System.out.println("5. resume - Възстанови паузиран разговор");
                System.out.println("6. status - Статус на системата");
                System.out.println("7. exit - Изход");
                System.out.print("Избери: ");
            }

            public static void showSystemStatus() {
                System.out.println("\n=== СТАТУС НА СИСТЕМАТА ===");
                System.out.print("Централа A - Заети: ");
                int busyCountA = 0;
                for (int i = 0; i < 26; i++) {
                    if (isBusyA[i]) {
                        System.out.print(cabinsA[i] + " ");
                        busyCountA++;
                    }
                }
                System.out.println("(" + busyCountA + "/26)");
                System.out.print("Централа B - Заети: ");
                int busyCountB = 0;
                for (int i = 0; i < 26; i++) {
                    if (isBusyB[i]) {
                        System.out.print(cabinsB[i] + " ");
                        busyCountB++;
                    }
                }
                System.out.println("(" + busyCountB + "/26)");
                System.out.println("Активни разговори: " + activeCallsCount);
                int activeServers = 0;
                for (int i = 0; i < 5; i++) {
                    if (serverBusy[i]) activeServers++;
                }
                System.out.println("Активни конферентни сървъри: " + activeServers + "/5");
            }

            public static void dial() {
                System.out.print("От коя кабинка се обаждате: ");
                String caller = sc.nextLine().trim();
                System.out.print("На коя кабинка се обаждате: ");
                String receiver = sc.nextLine().trim();
                CabinInfo callerInfo = findCabinInfo(caller);
                CabinInfo receiverInfo = findCabinInfo(receiver);
                if (callerInfo == null || receiverInfo == null) {
                    System.out.println("Невалидни кабинки!");
                    return;
                }
                if (!isCabinBusy(receiverInfo)) {
                    String code = generateCode();
                    setCabinBusy(callerInfo, true);
                    setCabinBusy(receiverInfo, true);
                    activeCodes[activeCallsCount] = code;
                    activeCaller[activeCallsCount] = caller;
                    activeReceiver[activeCallsCount] = receiver;
                    activeType[activeCallsCount] = "private";
                    activeCallsCount++;
                    System.out.println("CONNECTED → [ " + caller + receiver + " ]");
                    System.out.println("Код на разговора: " + code);
                } else {
                    handleBusyReceiver(caller, receiver, callerInfo, receiverInfo);
                }
            }

            public static void handleBusyReceiver(String caller, String receiver, CabinInfo callerInfo, CabinInfo receiverInfo) {
                System.out.println("Кабинка " + receiver + " е заета!");
                System.out.println("1. Приеми и затвори текущия разговор");
                System.out.println("2. Приеми и паузирай текущия разговор");
                System.out.println("3. Игнорирай разговора");
                System.out.print("Избери: ");
                int choice = sc.nextInt();
                sc.nextLine();
                if (choice == 1) {
                    endCallForCabin(receiver);
                    String code = generateCode();
                    setCabinBusy(callerInfo, true);
                    setCabinBusy(receiverInfo, true);
                    activeCodes[activeCallsCount] = code;
                    activeCaller[activeCallsCount] = caller;
                    activeReceiver[activeCallsCount] = receiver;
                    activeType[activeCallsCount] = "private";
                    activeCallsCount++;
                    System.out.println("CONNECTED → [ " + caller + receiver + " ]");
                    System.out.println("Код на разговора: " + code);
                } else if (choice == 2) {
                    int pausedCount = getPausedCount(receiverInfo);
                    if (pausedCount < 5) {
                        pauseCurrentCall(receiver, receiverInfo);
                        String code = generateCode();
                        setCabinBusy(callerInfo, true);
                        activeCodes[activeCallsCount] = code;
                        activeCaller[activeCallsCount] = caller;
                        activeReceiver[activeCallsCount] = receiver;
                        activeType[activeCallsCount] = "private";
                        activeCallsCount++;
                        System.out.println("CONNECTED → [ " + caller + receiver + " ]");
                        System.out.println("Код на разговора: " + code);
                    } else {
                        System.out.println("Не можете да паузирате повече разговори!");
                        System.out.println("1. Игнорирай  2. Затвори текущия");
                        int newChoice = sc.nextInt();
                        sc.nextLine();
                        if (newChoice == 2) {
                            handleBusyReceiver(caller, receiver, callerInfo, receiverInfo);
                        }
                    }
                } else {
                    System.out.println("Разговорът е игнориран.");
                }
            }

            public static void conference() {
                System.out.print("Домакин (кабинка): ");
                String host = sc.nextLine().trim();
                System.out.print("Участници (разделени със запетая): ");
                String participantsStr = sc.nextLine().trim();
                String[] participants = participantsStr.split(",");
                int serverIndex = -1;
                for (int i = 0; i < 5; i++) {
                    if (!serverBusy[i]) {
                        serverIndex = i;
                        break;
                    }
                }
                if (serverIndex == -1) {
                    int minParticipants = 21;
                    for (int i = 0; i < 5; i++) {
                        if (serverParticipantCount[i] < minParticipants) {
                            minParticipants = serverParticipantCount[i];
                            serverIndex = i;
                        }
                    }
                }
                String code = generateCode();
                conferenceCodes[serverIndex] = code;
                serverBusy[serverIndex] = true;
                serverParticipantCount[serverIndex] = 0;
                int baseIndex = serverIndex * 21;
                conferenceParticipants[baseIndex] = host.trim();
                serverParticipantCount[serverIndex] = 1;
                CabinInfo hostInfo = findCabinInfo(host.trim());
                if (hostInfo != null) setCabinBusy(hostInfo, true);
                for (String participant : participants) {
                    participant = participant.trim();
                    if (!participant.equals(host.trim()) && serverParticipantCount[serverIndex] < 20) {
                        conferenceParticipants[baseIndex + serverParticipantCount[serverIndex]] = participant;
                        CabinInfo pInfo = findCabinInfo(participant);
                        if (pInfo != null) setCabinBusy(pInfo, true);
                        serverParticipantCount[serverIndex]++;
                    }
                }
                System.out.println("КОНФЕРЕНТЕН РАЗГОВОР ЗАПОЧНАТ");
                System.out.println("Домакин: " + host);
                System.out.println("Код: " + code);
                System.out.print("Участници: ");
                for (int i = 1; i < serverParticipantCount[serverIndex]; i++) {
                    System.out.print(conferenceParticipants[baseIndex + i]);
                    if (i < serverParticipantCount[serverIndex] - 1) System.out.print(", ");
                }
                System.out.println();
            }

            public static void showAllCalls() {
                System.out.println("\n=== АКТИВНИ РАЗГОВОРИ ===");
                for (int i = 0; i < activeCallsCount; i++) {
                    if (activeType[i].equals("private")) {
                        System.out.print(activeCaller[i] + " → " + activeReceiver[i]);
                        CabinInfo receiverInfo = findCabinInfo(activeReceiver[i]);
                        if (receiverInfo != null) {
                            int pausedCount = getPausedCount(receiverInfo);
                            if (pausedCount > 0) {
                                System.out.print(" || ");
                                showPausedCalls(receiverInfo, pausedCount);
                            }
                        }
                        System.out.println(" (код: " + activeCodes[i] + ")");
                    }
                }
                for (int i = 0; i < 5; i++) {
                    if (serverBusy[i]) {
                        int baseIndex = i * 21;
                        System.out.print(conferenceParticipants[baseIndex] + " ↔ ");
                        for (int j = 1; j < serverParticipantCount[i]; j++) {
                            System.out.print(conferenceParticipants[baseIndex + j]);
                            if (j < serverParticipantCount[i] - 1) {
                                System.out.print(" <> ");
                            }
                        }
                        System.out.println(" (код: " + conferenceCodes[i] + ")");
                    }
                }
            }

            public static void endCall() {
                System.out.print("Код на разговора: ");
                String code = sc.nextLine().trim();
                for (int i = 0; i < activeCallsCount; i++) {
                    if (activeCodes[i].equals(code)) {
                        CabinInfo callerInfo = findCabinInfo(activeCaller[i]);
                        CabinInfo receiverInfo = findCabinInfo(activeReceiver[i]);
                        if (callerInfo != null) setCabinBusy(callerInfo, false);
                        if (receiverInfo != null) setCabinBusy(receiverInfo, false);
                        for (int j = i; j < activeCallsCount - 1; j++) {
                            activeCodes[j] = activeCodes[j + 1];
                            activeCaller[j] = activeCaller[j + 1];
                            activeReceiver[j] = activeReceiver[j + 1];
                            activeType[j] = activeType[j + 1];
                        }
                        activeCallsCount--;
                        System.out.println("Разговор " + code + " е прекратен.");
                        return;
                    }
                }
                for (int i = 0; i < 5; i++) {
                    if (serverBusy[i] && conferenceCodes[i].equals(code)) {
                        int baseIndex = i * 21;
                        for (int j = 0; j < serverParticipantCount[i]; j++) {
                            String participant = conferenceParticipants[baseIndex + j];
                            if (participant != null) {
                                CabinInfo pInfo = findCabinInfo(participant);
                                if (pInfo != null) setCabinBusy(pInfo, false);
                                conferenceParticipants[baseIndex + j] = null;
                            }
                        }
                        serverBusy[i] = false;
                        conferenceCodes[i] = null;
                        serverParticipantCount[i] = 0;
                        System.out.println("Конферентен разговор " + code + " е прекратен.");
                        return;
                    }
                }
                System.out.println("Разговор с код " + code + " не е намерен!");
            }

            public static void resumeCall() {
                System.out.print("Кабинка: ");
                String cabin = sc.nextLine().trim();
                CabinInfo cabinInfo = findCabinInfo(cabin);
                if (cabinInfo == null) {
                    System.out.println("Невалидна кабинка!");
                    return;
                }
                int pausedCount = getPausedCount(cabinInfo);
                if (pausedCount == 0) {
                    System.out.println("Няма паузирани разговори!");
                    return;
                }
                System.out.println("Паузирани разговори:");
                showPausedCallsWithNumbers(cabinInfo, pausedCount);
                System.out.print("Избери номер: ");
                int choice = sc.nextInt();
                sc.nextLine();
                if (choice >= 0 && choice < pausedCount) {
                    if (isCabinBusy(cabinInfo)) {
                        pauseCurrentCall(cabin, cabinInfo);
                    }
                    String[] pausedCodes = (cabinInfo.isA) ? pausedCodesA : pausedCodesB;
                    String[] pausedCallers = (cabinInfo.isA) ? pausedCallersA : pausedCallersB;
                    String[] pausedReceivers = (cabinInfo.isA) ? pausedReceiversA : pausedReceiversB;
                    int[] pausedCounts = (cabinInfo.isA) ? pausedCountA : pausedCountB;
                    int pauseIndex = cabinInfo.index * 5 + choice;
                    String code = pausedCodes[pauseIndex];
                    String caller = pausedCallers[pauseIndex];
                    String receiver = pausedReceivers[pauseIndex];
                    for (int i = choice; i < pausedCounts[cabinInfo.index] - 1; i++) {
                        int currentPauseIndex = cabinInfo.index * 5 + i;
                        int nextPauseIndex = cabinInfo.index * 5 + i + 1;
                        pausedCodes[currentPauseIndex] = pausedCodes[nextPauseIndex];
                        pausedCallers[currentPauseIndex] = pausedCallers[nextPauseIndex];
                        pausedReceivers[currentPauseIndex] = pausedReceivers[nextPauseIndex];
                    }
                    pausedCounts[cabinInfo.index]--;
                    activeCodes[activeCallsCount] = code;
                    activeCaller[activeCallsCount] = caller;
                    activeReceiver[activeCallsCount] = receiver;
                    activeType[activeCallsCount] = "private";
                    activeCallsCount++;
                    setCabinBusy(cabinInfo, true);
                    System.out.println("Възстановен разговор: " + caller + " → " + receiver);
                }
            }

            public static class CabinInfo {
                public boolean isA;
                public int index;
                public String name;
                public CabinInfo(boolean isA, int index, String name) {
                    this.isA = isA;
                    this.index = index;
                    this.name = name;
                }
            }

            public static CabinInfo findCabinInfo(String cabin) {
                for (int i = 0; i < 26; i++) {
                    if (cabinsA[i].equals(cabin)) {
                        return new CabinInfo(true, i, cabin);
                    }
                }
                for (int i = 0; i < 26; i++) {
                    if (cabinsB[i].equals(cabin)) {
                        return new CabinInfo(false, i, cabin);
                    }
                }
                return null;
            }

            public static boolean isCabinBusy(CabinInfo cabinInfo) {
                return cabinInfo.isA ? isBusyA[cabinInfo.index] : isBusyB[cabinInfo.index];
            }

            public static void setCabinBusy(CabinInfo cabinInfo, boolean busy) {
                if (cabinInfo.isA) {
                    isBusyA[cabinInfo.index] = busy;
                } else {
                    isBusyB[cabinInfo.index] = busy;
                }
            }

            public static int getPausedCount(CabinInfo cabinInfo) {
                return cabinInfo.isA ? pausedCountA[cabinInfo.index] : pausedCountB[cabinInfo.index];
            }

            public static void showPausedCalls(CabinInfo cabinInfo, int count) {
                String[] pausedCallers = cabinInfo.isA ? pausedCallersA : pausedCallersB;
                for (int j = 0; j < count; j++) {
                    int pauseIndex = cabinInfo.index * 5 + j;
                    System.out.print(pausedCallers[pauseIndex]);
                    if (j < count - 1) System.out.print(", ");
                }
            }

            public static void showPausedCallsWithNumbers(CabinInfo cabinInfo, int count) {
                String[] pausedCallers = cabinInfo.isA ? pausedCallersA : pausedCallersB;
                String[] pausedReceivers = cabinInfo.isA ? pausedReceiversA : pausedReceiversB;
                for (int i = 0; i < count; i++) {
                    int pauseIndex = cabinInfo.index * 5 + i;
                    System.out.println(i + ": " + pausedCallers[pauseIndex] +
                            " → " + pausedReceivers[pauseIndex]);
                }
            }

            public static String generateCode() {
                return String.format("%03d", rand.nextInt(900) + 100);
            }

            public static void endCallForCabin(String cabin) {
                for (int i = 0; i < activeCallsCount; i++) {
                    if (activeCaller[i].equals(cabin) || activeReceiver[i].equals(cabin)) {
                        CabinInfo callerInfo = findCabinInfo(activeCaller[i]);
                        CabinInfo receiverInfo = findCabinInfo(activeReceiver[i]);
                        if (callerInfo != null) setCabinBusy(callerInfo, false);
                        if (receiverInfo != null) setCabinBusy(receiverInfo, false);
                        for (int j = i; j < activeCallsCount - 1; j++) {
                            activeCodes[j] = activeCodes[j + 1];
                            activeCaller[j] = activeCaller[j + 1];
                            activeReceiver[j] = activeReceiver[j + 1];
                            activeType[j] = activeType[j + 1];
                        }
                        activeCallsCount--;
                        break;
                    }
                }
            }

            public static void pauseCurrentCall(String cabin, CabinInfo cabinInfo) {
                for (int i = 0; i < activeCallsCount; i++) {
                    if (activeReceiver[i].equals(cabin)) {
                        String[] pausedCodes = cabinInfo.isA ? pausedCodesA : pausedCodesB;
                        String[] pausedCallers = cabinInfo.isA ? pausedCallersA : pausedCallersB;
                        String[] pausedReceivers = cabinInfo.isA ? pausedReceiversA : pausedReceiversB;
                        int[] pausedCounts = cabinInfo.isA ? pausedCountA : pausedCountB;
                        int pauseIndex = cabinInfo.index * 5 + pausedCounts[cabinInfo.index];
                        pausedCodes[pauseIndex] = activeCodes[i];
                        pausedCallers[pauseIndex] = activeCaller[i];
                        pausedReceivers[pauseIndex] = activeReceiver[i];
                        pausedCounts[cabinInfo.index]++;
                        for (int j = i; j < activeCallsCount - 1; j++) {
                            activeCodes[j] = activeCodes[j + 1];
                            activeCaller[j] = activeCaller[j + 1];
                            activeReceiver[j] = activeReceiver[j + 1];
                            activeType[j] = activeType[j + 1];
                        }
                        activeCallsCount--;
                        break;
                    }
                }
            }
        }