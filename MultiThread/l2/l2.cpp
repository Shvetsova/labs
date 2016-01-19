// l2.cpp: ���������� ����� ����� ��� ����������� ����������.
//

#include <stdio.h>  
#include <iostream>  
#include <time.h> 
#include <fstream>
#include <math.h>
#include <mpi.h>
#include <stdlib.h>
#include <sstream> 
#include <stdexcept>

#define ARG_COUNT 5

# define EXIT_APP(RANK,MESS) if((RANK) == 0) { \
		      std::cout << (MESS) << std::endl; \
		      } \
		      MPI_Finalize();\
		      return 0;

void Jacobi(int M, double** A, double* F, double* X, double eps) {
	int rank, size;
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);// ������ ������������� ��������
	MPI_Comm_size(MPI_COMM_WORLD, &size);// ������ ���������� ���������

	double* TempX = (double *)calloc(sizeof(double), M); //������ ������������� �����������
	double norm = 1;// ���������� �� ���� ��������� ������������ TempNorm
	double TempNorm = 1;// �������������� � ������ ��������

	double mtime = 0;
	if (rank == 0) {
		mtime = MPI_Wtime();// �������� ������� �������� ������� ��� ������� ��������
	}

	do {
		MPI_Bcast(X, M, MPI_DOUBLE, 0, MPI_COMM_WORLD); // ��������� ���� ��������� �������� �������� �����������
		for (int i = rank; i < M; i += size) {// ������ ������� ������� ���� ����� �����
			TempX[i] = F[i];
			for (int g = 0; g < M; g++) {
				if (i != g)
					TempX[i] -= A[i][g] * X[g];
			}
			TempX[i] /= A[i][i];
			//������� �������������� �������
		}
		MPI_Barrier(MPI_COMM_WORLD);// ���� ���� �������� ������ �� ���� �����
		TempNorm = abs(X[0] - TempX[0]); // ������� TempNorm �� ������
		for (int h = rank; h < M; h += size) {// ������ ������� ����� �� ����� �����, ���� ������������ TempNorm
			if (abs(X[h] - TempX[h]) > TempNorm)
				TempNorm = abs(X[h] - TempX[h]);
		}
		MPI_Barrier(MPI_COMM_WORLD);// ���� ���� �������� ������ �� ���� �����
		MPI_Reduce(&TempNorm, &norm, 1, MPI_DOUBLE, MPI_MAX, 0, MPI_COMM_WORLD);// ���� �������� �������� ������������ TempNorm � ���������� � norm
		MPI_Reduce(TempX, X, M, MPI_DOUBLE, MPI_SUM, 0, MPI_COMM_WORLD);// �������� �� ���� ������������� ����������� �����

	} while (norm > eps);//���� ���� ������ ��������
	free(TempX);// ���������� tempx

	if (rank == 0) {// ���� ������� �������
		mtime = MPI_Wtime() - mtime;
		std::cout << "calculation time : " << mtime << std::endl; // ������� �������� ������������ �������
	}

}

int Converge(int M, double** A) { // ������� ���������
	int rank, size;
	MPI_Comm_rank(MPI_COMM_WORLD, &rank); // ������ ������������� ��������
	MPI_Comm_size(MPI_COMM_WORLD, &size);// ������ ���������� ���������

	for (int i = rank; i < M; i += size) {    // ����� � ����� ��������� �� �������������� �������� � ���������� ���������
		double sum = 0.0;
		for (int j = 0; j < M; j++) {
			if (i != j) {
				sum += abs(A[i][j]); // ����� ������� �������������� ���������
			}
		}
		if (abs(A[i][i]) < sum) {// ���� ����� ������� �������������� ��������� ������ ������ ������������� ��������
			throw std::logic_error("not converge");
		}
	}
	MPI_Barrier(MPI_COMM_WORLD);
}
int main(int argc, char **argv) {

	int rank;
	MPI_Init(&argc, &argv); //������������� mpi
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);// ��������� �������������� ��������

										 /*
										 *  argv[1] - input file coefficient matrix
										 *  argv[2] - input file initial approximation
										 *  argv[3] - accuracy value
										 *  argv[4] - output file
										 */

	if (argc != ARG_COUNT) {
		EXIT_APP(rank, " incorrect number of arguments ");
	}

	double eps = atof(argv[3]); // ����������� 

	if (eps <= 0 || eps >= 1) {// ���� �� �� 0 �� 1
		EXIT_APP(rank, " incorrect eps ");
	}

	std::ifstream fmatrix(argv[1]);
	std::ifstream fapprox(argv[2]);
	std::ofstream fout(argv[4]);

	if (!fmatrix.is_open() || !fapprox.is_open() || !fout.is_open()) {// ���� �� ��������� �����
		EXIT_APP(rank, " could not open file ");
	}

	// --> matrix file read
	int M;// ���������� �����
	int N;//  ���������� ��������
	double** A;//������� �������������
	double* F;//��������� �����
	

	std::string line;
	std::getline(fmatrix, line);
	std::istringstream iss(line);
	if (!(iss >> M >> N)) {
		EXIT_APP(rank, " matrix file could not get M and N ");
	}
	if (N != M + 1) { // ���� �������� �� �� ���� ������ �����
		EXIT_APP(rank, " matrix incorrect N != M + 1 ");
	}

	A = (double**)malloc(sizeof(double*) * M);// ��������� ������ ��� ��������� �� �������
	F = (double*)malloc(sizeof(double) * M);// ��������� ������ ��� ������

	for (int i = 0; i < M; i++) {
		line = "";// ������ ������ ������
		std::getline(fmatrix, line);// ������ �� �������� ������ � ��������
		if (line.size() < 1) {// ���� ������ ������ 1 �������
			EXIT_APP(rank, " matrix line incorrect ");
		}
		A[i] = (double*)malloc(sizeof(double) * M);// �������� ������ ��� ������ - ������

		std::istringstream issl(line);
		for (int j = 0; j < M; j++) {// ����� �� ���������� ��������� � �������
			if (!(issl >> A[i][j])) {// ���� ��� �������� � ������� 
				EXIT_APP(rank, " matrix line incorrect ");
			}
		}

		if (!(issl >> F[i])) {// ��������� ������� � ������ ������������ � f
			EXIT_APP(rank, " matrix line incorrect ");
		}
	}
	// <-- matrix file read

	// --> approximation
	double* X;// ������� �����������
	int aM;// ���������� �����
	line = "";
	std::getline(fapprox, line);
	std::istringstream aiss(line);

	if (!(aiss >> aM)) {
		EXIT_APP(rank, " approximation file could not get M ");
	}
	if (M != aM) {// ���� ���������� ����� � ������� �� ����� ���������� ����� � �����������
		EXIT_APP(rank, " approximation file M does not match matrix M ");
	}

	X = (double*)calloc(sizeof(double), M);// �������� ������ ��� ������ �����������

	for (int i = 0; i < M; i++) {//
		line = "";
		std::getline(fapprox, line);
		if (line.size() < 1) {
			EXIT_APP(rank, " approximation line incorrect ");
		}
		std::istringstream aissl(line);
		if (!(aissl >> X[i])) {
			EXIT_APP(rank, " approximation line incorrect ");
		}
	}
	// <-- approximation

	MPI_Barrier(MPI_COMM_WORLD); //  ��������� ���� ��� �������� ������ �� ���� �����
	try {

		Converge(M, A);// �������� ����������
		Jacobi(M, A, F, X, eps); // ���������� ����
	
	}
	catch (std::logic_error e) {
		EXIT_APP(rank, e.what());
	}
	
	// --> output  

	if (rank == 0) {
		fout << M << std::endl;
		for (int i = 0; i < M; i++) {
			fout << X[i] << std::endl;
		}
	}

	// <-- output

	fmatrix.close();
	fapprox.close();
	fout.close();

	EXIT_APP(rank, "Succeeded")
}
