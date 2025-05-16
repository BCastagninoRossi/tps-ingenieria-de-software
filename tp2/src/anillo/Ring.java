package anillo;

import java.util.Stack;

public class Ring {
    Object   cargo;
    Ring     next;
    RingState state;
    private static Stack<RemoveLogic> functionStack = new Stack<>();

    public Ring() {
        this.cargo = null;
        this.next  = this;
        this.state = new EmptyRingState();
    }

    public Ring next() {
        return state.next(this);
    }

    public Object current() {
        return state.current(this);
    }

    public Ring add(Object cargo) {
        return state.add(this, cargo);
    }

    public Ring remove() {
        return state.remove(this);
    }

    abstract static class RingState {
        abstract Ring next(Ring ctx);
        abstract Object current(Ring ctx);
        abstract Ring add(Ring ctx, Object cargo);
        abstract Ring remove(Ring ctx);
    }


    static class EmptyRingState extends RingState {
        Ring next(Ring ctx) {
            throw new IllegalStateException("No se puede avanzar en un anillo vacío.");
        }

        Object current(Ring ctx) {
            throw new IllegalStateException("No hay elemento actual en un anillo vacío.");
        }

        Ring add(Ring ctx, Object cargo) {
            ctx.cargo = cargo;
            ctx.next  = ctx;
            ctx.state = new NonEmptyRingState();
            functionStack.push(new FinalRemoveLogic());
            return ctx;
        }

        Ring remove(Ring ctx) {
            return ctx;
        }
    }

    static class NonEmptyRingState extends RingState {
        Ring next(Ring ctx) {
            return ctx.next;
        }

        Object current(Ring ctx) {
            return ctx.cargo;
        }

        Ring add(Ring ctx, Object cargo) {
            Ring newNode = new Ring();
            newNode.cargo = ctx.cargo;
            newNode.next  = ctx.next;
            newNode.state = ctx.state;
            ctx.cargo = cargo;
            ctx.next  = newNode;
            functionStack.push(new RegularRemoveLogic());
            return ctx;
        }

        Ring remove(Ring ctx) {
            RemoveLogic logicFunction = functionStack.pop();
            return logicFunction.apply(ctx);
        }
    }

    abstract static class RemoveLogic {
        abstract Ring apply(Ring ctx);
    }

    static class FinalRemoveLogic extends RemoveLogic {
        Ring apply(Ring ctx) {
            ctx.cargo = null;
            ctx.next  = ctx;
            ctx.state = new EmptyRingState();
            return ctx;
        }
    }

    static class RegularRemoveLogic extends RemoveLogic {
        Ring apply(Ring ctx) {
            ctx.cargo = ctx.next.cargo;
            ctx.state = ctx.next.state;
            ctx.next  = ctx.next.next;
            return ctx;
        }
    }
}
