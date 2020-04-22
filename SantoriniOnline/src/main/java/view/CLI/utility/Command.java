package view.CLI.utility;

import model.TurnAction;
import model.gamemap.BlockTypeEnum;
import model.gamemap.Worker;

public class Command {

    //Native
    private final String x;
    private final String y;
    private final String worker;
    private final String block;
    private final String com;

    //Converted
    int fx, fy;
    Worker.IDs fw;
    TurnAction action;
    BlockTypeEnum fblock;

    public Command(String com, String x, String y, String id, String block) {
        this.x = x;
        this.y = y;
        this.worker = id;
        this.block = block;
        this.com = com;
    }


    public boolean extract() {

        try {
            fx = Integer.parseInt(x);
            fy = Integer.parseInt(y);
        } catch (NumberFormatException e) {
            return false;
        }

        if (Worker.IDs.valueOf(worker.toUpperCase()) == Worker.IDs.A) {
            fw = Worker.IDs.A;
        } else if (Worker.IDs.valueOf(worker.toUpperCase()) == Worker.IDs.B) {
            fw = Worker.IDs.B;

        } else {
            return false;
        }

        if (com.equals("move")) {
            action = TurnAction.MOVE;
            fblock = null;
        } else if (com.equals("build")) {
            action = TurnAction.BUILD;
            if (block != null) {
                switch (block) {
                    case ("1"):
                        fblock = BlockTypeEnum.LEVEL1;
                        break;
                    case ("2"):
                        fblock = BlockTypeEnum.LEVEL2;
                        break;
                    case ("3"):
                        fblock = BlockTypeEnum.LEVEL3;
                        break;
                    case ("d"):
                        fblock = BlockTypeEnum.DOME;
                        break;
                    case (""):
                        fblock = null;
                        break;
                    default:
                        return false;
                }
            }
        }  else {
            return false;
        }


        return true;
    }

    public int getFRow() {
        return fx;
    }

    public int getFColumn() {
        return fy;
    }

    public Worker.IDs getFWorker() {
        return fw;
    }

    public TurnAction getFAction() {
        return action;
    }

    public BlockTypeEnum getFBlock() {
        return fblock;
    }
}
