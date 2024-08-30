import socket
import pickle
import pygame
import random

# Define constants
BOARD_WIDTH = 10
BOARD_HEIGHT = 20
CELL_SIZE = 30
SCREEN_WIDTH = BOARD_WIDTH * CELL_SIZE
SCREEN_HEIGHT = BOARD_HEIGHT * CELL_SIZE
FPS = 10  # Frames per second

TETROMINOES = [
    [[1, 1, 1, 1]],  # I shape
    [[1, 1], [1, 1]],  # O shape
    [[0, 1, 0], [1, 1, 1]],  # T shape
    [[1, 1, 0], [0, 1, 1]],  # S shape
    [[0, 1, 1], [1, 1, 0]],  # Z shape
    [[1, 1, 1], [1, 0, 0]],  # L shape
    [[1, 1, 1], [0, 0, 1]]   # J shape
]

class Tetris:
    def __init__(self):
        self.board = [[0] * BOARD_WIDTH for _ in range(BOARD_HEIGHT)]
        self.current_tetromino = self.get_new_tetromino()
        self.tetromino_x = BOARD_WIDTH // 2 - len(self.current_tetromino[0]) // 2
        self.tetromino_y = 0
        self.game_over = False

    def get_new_tetromino(self):
        return random.choice(TETROMINOES)

    def check_collision(self, offset_x=0, offset_y=0):
        for y, row in enumerate(self.current_tetromino):
            for x, cell in enumerate(row):
                if cell:
                    new_x = x + self.tetromino_x + offset_x
                    new_y = y + self.tetromino_y + offset_y
                    if (new_x < 0 or new_x >= BOARD_WIDTH or
                        new_y >= BOARD_HEIGHT or
                        self.board[new_y][new_x]):
                        return True
        return False

    def move(self, direction):
        if not self.game_over:
            if direction == 'left' and not self.check_collision(offset_x=-1):
                self.tetromino_x -= 1
            elif direction == 'right' and not self.check_collision(offset_x=1):
                self.tetromino_x += 1
            elif direction == 'down':
                if not self.check_collision(offset_y=1):
                    self.tetromino_y += 1
                else:
                    self.lock_tetromino()
            elif direction == 'rotate':
                rotated_tetromino = list(zip(*self.current_tetromino[::-1]))
                if not self.check_collision():
                    self.current_tetromino = rotated_tetromino

    def drop(self):
        if not self.game_over:
            while not self.check_collision(offset_y=1):
                self.tetromino_y += 1
            self.lock_tetromino()

    def lock_tetromino(self):
        for y, row in enumerate(self.current_tetromino):
            for x, cell in enumerate(row):
                if cell:
                    self.board[y + self.tetromino_y][x + self.tetromino_x] = cell
        self.clear_lines()
        self.current_tetromino = self.get_new_tetromino()
        self.tetromino_x = BOARD_WIDTH // 2 - len(self.current_tetromino[0]) // 2
        self.tetromino_y = 0
        if self.check_collision():
            self.game_over = True

    def clear_lines(self):
        lines_to_clear = [i for i, row in enumerate(self.board) if all(row)]
        for i in lines_to_clear:
            del self.board[i]
            self.board.insert(0, [0] * BOARD_WIDTH)

    def get_board(self):
        return self.board

def handle_client(conn, tetris):
    while not tetris.game_over:
        try:
            command = conn.recv(1024).decode('utf-8')
            if command == "LEFT":
                tetris.move('left')
            elif command == "RIGHT":
                tetris.move('right')
            elif command == "DOWN":
                tetris.move('down')
            elif command == "ROTATE":
                tetris.move('rotate')

            # Send the updated board back to the client
            conn.sendall(pickle.dumps(tetris.get_board()))

        except Exception as e:
            print(f"Error: {e}")
            break

    conn.close()

def start_server():
    tetris = Tetris()

    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind(('localhost', 5000))
    server_socket.listen(1)
    print("Server started, waiting for a connection...")

    while not tetris.game_over:
        conn, addr = server_socket.accept()
        print(f"Connected by {addr}")
        handle_client(conn, tetris)

    server_socket.close()

if __name__ == "__main__":
    start_server()
