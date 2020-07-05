package com.ql.util.express.instruction.factory;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.instruction.ForRelBreakContinue;
import com.ql.util.express.instruction.detail.InstructionClearDataStack;
import com.ql.util.express.instruction.detail.InstructionCloseNewArea;
import com.ql.util.express.instruction.detail.InstructionOpenNewArea;
import com.ql.util.express.parse.ExpressNode;

import java.util.Stack;

/**
 * 代码块指令工厂
 */
public class BlockInstructionFactory extends InstructionFactory {

    public boolean createInstruction(ExpressRunner aCompile, InstructionSet result,
                                     Stack<ForRelBreakContinue> forStack, ExpressNode node, boolean isRoot)
            throws Exception {
        //分号？
        if (node.isTypeEqualsOrChild("STAT_SEMICOLON")
                && result.getCurrentPoint() >= 0
                && result.getInstruction(result.getCurrentPoint()) instanceof InstructionClearDataStack == false) {
            //添加一个清除运行时数据的指令
            result.addInstruction(new InstructionClearDataStack().setLine(node.getLine()));
        }

        int tmpPoint = result.getCurrentPoint() + 1;
        boolean returnVal = false;
        boolean hasDef = false;

        ExpressNode[] allChildren = node.getChildren();
        for (ExpressNode tmpNode : allChildren) {
            boolean tmpHas = aCompile.createInstructionSetPrivate(result, forStack, tmpNode, false);
            hasDef = hasDef || tmpHas;
        }

        if (hasDef == true && isRoot == false
                && node.getTreeType().isEqualsOrChild("STAT_BLOCK")) {
            result.insertInstruction(tmpPoint, new InstructionOpenNewArea().setLine(node.getLine()));
            result.insertInstruction(result.getCurrentPoint() + 1, new InstructionCloseNewArea().setLine(node.getLine()));
            returnVal = false;
        } else {
            returnVal = hasDef;
        }
        return returnVal;
    }
}
