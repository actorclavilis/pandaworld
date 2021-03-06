/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package student.grid;

import java.rmi.Remote;
import java.rmi.RemoteException;
import student.remote.server.RemoteCritter;

/**
 *
 * @author haro
 */
public interface RTile extends Remote {

    boolean critter() throws RemoteException;

    boolean food() throws RemoteException;

    int foodValue() throws RemoteException;

    RemoteCritter getCritter() throws RemoteException;

    boolean isEmpty() throws RemoteException;

    boolean plant() throws RemoteException;

    boolean rock() throws RemoteException;
}
