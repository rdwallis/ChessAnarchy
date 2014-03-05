package com.wallissoftware.chessanarchy.shared.game;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.wallissoftware.chessanarchy.shared.game.exceptions.IllegalMoveException;

public class ABoardShould {

	@Test
	public void playTheseGameWithoutIssue() {
		//test en passant, promtion + castling
		playGame("e4", "d5", "e5", "f5", "exf6", "d4", "fxg7", "Nc6", "gxh8=Q", "Be6", "c4", "Qd7", "b4", "O-O-O", "b5");
		System.out.println("FIRST TEST PASSED");

		//tests a couple of duplicate moves
		playGame("d4", "Nf6", "Nf3", "c5", "d5", "b5", "Bg5", "d6", "Bxf6", "exf6", "e4", "a6", "a4", "b4", "Bd3", "g6", "Nbd2", "Bg7", "O-O", "O-O", "Nc4", "Bg4", "Be2", "f5", "Nfd2", "Bxe2", "Qxe2", "f4", "Qg4", "Qf6", "Rae1", "h5", "Qh3", "Ra7", "e5", "dxe5", "Nxe5", "Re7",
				"Nd3", "Ree8", "Qf3", "Rd8", "Ne4", "Qf5", "d6", "Nd7", "Re2", "a5", "Rd1", "Rde8", "b3", "Bh6", "Nd2", "Rxe2", "Qxe2", "h4", "Re1", "h3", "Nc4", "hxg2", "Kxg2", "Nf6", "Qf3", "Re8", "Rxe8+", "Nxe8", "Nde5", "Nf6", "Qd3", "Qc8", "Nxa5", "Nd7", "Ng4", "Bg7",
				"Nc4", "Qe8", "h3", "Qe1", "Qf3", "f5", "Nh2", "g5", "Qd5+", "Kh8", "Nf3", "Qe8", "Qxf5", "Bf6", "Nxg5", "Kg7", "Ne6+", "Kg8", "Nxf4", "Kf7", "Qh7+", "Bg7", "Qg6+", "Kg8", "Qxe8+");

		System.out.println("SECOND TEST PASSED");

		playGame("e4", "e5", "f4", "d5", "d4", "dxe4", "fxe5", "Be6", "Bb5+", "c6", "Be2", "Nh6", "Bxh6", "Qh4+", "g3", "Qxh6", "Nd2", "e3", "Nf1", "Bb4+", "c3", "Be7", "Qd3", "Bg5", "h4", "O-O", "Nh3", "Be7", "Nf4", "Nd7", "Nxe3", "Rad8", "Qe4", "Kh8", "O-O-O", "Nb6", "Nxe6",
				"Qxe6", "Nf5", "Nc8", "Kb1", "f6", "h5", "fxe5", "Bd3", "Qd5", "dxe5", "Bc5", "e6", "a5", "Qg4", "Qe5", "Rhe1");

		System.out.println("THIRD TEST PASSED");

		playGame("c4", "Nf6", "Nc3", "e6", "Nf3", "c5", "e3", "Be7", "d4", "a6", "Bd3", "d5", "O-O", "O-O", "b3", "Nc6", "Bb2", "dxc4", "bxc4", "Nb4", "Be2", "Bd7", "a3", "cxd4", "exd4", "Nc6", "d5", "Qc7", "dxc6", "Bxc6", "Qc1", "Bd6", "g3", "Rad8", "Nd4", "Rfe8", "Qg5", "Be7",
				"Rfd1", "Bd7", "Rab1", "h6", "Qe3", "e5", "Nb3", "Bf5", "Rbc1", "Qb8", "Na4", "Ne4", "Qf3", "Bg6", "c5", "Qc7", "Nb6", "Rxd1+", "Rxd1", "Bxc5", "Nxc5", "Nxc5", "Nc4", "b5", "Nd6", "Qxd6", "Rxd6", "Nd3", "Rxd3", "Re6", "Bxe5", "Bxd3", "Bxd3", "Re8", "Qf5", "Re6",
				"Qh7+", "Kf8", "Qh8+", "Ke7", "Bxg7", "Kd6", "Qf8+", "Kc6", "Qc8+", "Kd5", "Bf8", "Re1+", "Kg2", "Kd4", "Bc2", "Re5", "Bb4", "a5", "Qg4+", "Kd5", "Qd7+", "Kc4", "Qd3#");

		System.out.println("FORTH TEST PASSED");

	}

	public void playGame(final String... moves) {
		long start = System.currentTimeMillis();
		playGameOnBoard(moves);
		final long boardTime = System.currentTimeMillis() - start;
		start = System.currentTimeMillis();
		playGameOnMoveNode(moves);
		final long moveNode = System.currentTimeMillis() - start;
		if (moveNode < boardTime) {
			System.out.println("MoveNode is " + (boardTime - moveNode) + "ms faster than board");
		} else {
			System.out.println("Board is " + (moveNode - boardTime) + "ms faster than MoveNode");
		}
	}

	public void playGameOnBoard(final String... moves) {
		final List<String> moveList = new ArrayList<String>();
		for (int i = 0; i < moves.length; i++) {
			moveList.add(moves[i]);
		}
		Board board = null;
		try {
			board = new Board(moveList.subList(0, moveList.size() - 1));
			board.resetFromMoveList(moveList);
			//board.printBoard();

		} catch (final IllegalMoveException e) {
			if (board != null) {
				board.printBoard();
			}
			fail();
		}
	}

	public void playGameOnMoveNode(final String... moves) {
		final List<String> moveList = new ArrayList<String>();
		for (int i = 0; i < moves.length; i++) {
			moveList.add(moves[i]);
		}
		try {
			MoveNode.getBoard(moveList);

		} catch (final IllegalMoveException e) {
			fail();
		}
	}

}
