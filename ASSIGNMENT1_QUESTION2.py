# LINEAR OPTIMIZATION
# ASSIGNMENT - 1
# QUESTION - 2

# GROUP MEMBER:
# JATIN CHAUHAN - CS17BTECH11019
# GAJANAN SHETKAR - CS17BTECH11016
# ANURAG PATIL - CS17BTECH11004

##################################################################################
import numpy

##################################################################################
# Simplex method(For degenerate case)
def Simplex_method(dimension_m_of_A, dimension_n_of_A, matrix_A, vector_B, vector_C):
    
    vector_C = -1 * vector_C

    # print("Matrix A : ")
    # print(matrix_A)
    # print("Vector B : ")
    # print(vector_B)
    # print("Vector C : ")
    # print(vector_C)

    # Adding Slack variable
    matrix_A = numpy.hstack((matrix_A, numpy.identity(matrix_A.shape[0], dtype=matrix_A.dtype)))
    # print("Matrix A after adding Slack variable : ")
    # print(matrix_A)

    vector_C = numpy.concatenate((vector_C, numpy.zeros((dimension_m_of_A), dtype=float)))
    # print("Matrix C after adding Slack variable : ")
    # print(vector_C)

    # Initialization step of algorithm(Iniitialization with a one of feasible solution(corner point))
    # vector_X = numpy.concatenate((numpy.zeros((dimension_n_of_A), dtype=float), vector_B))
    # print("Initialzation of vector_X : ")
    # print(vector_X)

    # basic variable and non basic variable index
    vector_basic_variable_index = numpy.arange(start=dimension_n_of_A, stop=dimension_n_of_A + dimension_m_of_A, step=1)
    vector_non_basic_variable_index = numpy.arange(start=0, stop=dimension_n_of_A, step=1)
    # print("basic variable index : ")
    # print(vector_basic_variable_index)
    # print("non basic variable index : ")
    # print(vector_non_basic_variable_index)

    # matrix_basic_variable and matrix_non_basic_variable initialization
    matrix_basic_variable = numpy.zeros((dimension_m_of_A, dimension_m_of_A), dtype=float)
    matrix_non_basic_variable = numpy.zeros((dimension_m_of_A, dimension_n_of_A), dtype=float)
    for i in range(dimension_m_of_A):
        matrix_basic_variable[:, i] = matrix_A[:, vector_basic_variable_index[i]].copy()
    for i in range(dimension_n_of_A):
        matrix_non_basic_variable[:, i] = matrix_A[:, vector_non_basic_variable_index[i]].copy()
    # print("B = ")
    # print(matrix_basic_variable)
    # print("N = ")
    # print(matrix_non_basic_variable)

    # vector c is diveded in it's basic variable part and non basic variable part.
    vector_C_basic_variable = numpy.zeros((dimension_m_of_A), dtype=float)
    vector_C_non_basic_variable = numpy.zeros((dimension_n_of_A), dtype=float)
    for i in range(dimension_m_of_A):
        vector_C_basic_variable[i] = vector_C[vector_basic_variable_index[i]]
    for i in range(dimension_n_of_A):
        vector_C_non_basic_variable[i] = vector_C[vector_non_basic_variable_index[i]]
    # print("C(b) = ")
    # print(vector_C_basic_variable)
    # print("C(n) = ")
    # print(vector_C_non_basic_variable)

    vector_X_basic_variable = numpy.matmul(numpy.linalg.inv(matrix_basic_variable), vector_B.transpose())
    # print('X(b) = ')
    # print(vector_X_basic_variable)

    vector_Y = numpy.matmul(vector_C_basic_variable, numpy.linalg.inv(matrix_basic_variable))
    # print('y = ')
    # print(vector_Y)
    # print('Objective Value : ' + str(numpy.matmul(vector_Y, vector_B.transpose())))
    Objective_value = -1 * numpy.matmul(vector_Y, vector_B.transpose())
    previous_Objective_value = Objective_value

    is_unbounded = 0

    ###############################################################################################################
    # Convert all min to max

    vector_C_non_basic_variable_new = vector_C_non_basic_variable - numpy.matmul(vector_Y, matrix_non_basic_variable)

    while(any((x < 0) for x in vector_C_non_basic_variable_new)):
        entering_index = vector_non_basic_variable_index[vector_C_non_basic_variable_new.argmin()]
        entering_column = numpy.matmul(numpy.linalg.inv(matrix_basic_variable), matrix_A[:, entering_index])
        if(all((y <= 0) for y in entering_column)):
            print("Error !!!!!!!!!!!!!!!! This problem is an unbounded problem.")
            is_unbounded = 1
            break
        leaving_index = vector_basic_variable_index[0]
        minimum_ratio = numpy.amax(vector_X_basic_variable) / 0.00000001
        # print('enternig index : ')
        # print(entering_index)
        # print('enterning column : ')
        # print(entering_column)
        for i in range(entering_column.shape[0]):
            if((entering_column[i] > 0) and (minimum_ratio > (vector_X_basic_variable[i] / entering_column[i]))):
                minimum_ratio = (vector_X_basic_variable[i] / entering_column[i])
                leaving_index = vector_basic_variable_index[i]
        # print('leaving index : ')
        # print(leaving_index)
        for i in range(vector_non_basic_variable_index.shape[0]):
            if(vector_non_basic_variable_index[i] == entering_index):
                vector_non_basic_variable_index[i] = leaving_index
        for i in range(vector_basic_variable_index.shape[0]):
            if(vector_basic_variable_index[i] == leaving_index):
                vector_basic_variable_index[i] = entering_index

        
        # print("basic variable index : ")
        # print(vector_basic_variable_index)
        # print("non basic variable index : ")
        # print(vector_non_basic_variable_index)
        
        matrix_basic_variable = numpy.zeros((dimension_m_of_A, dimension_m_of_A), dtype=float)
        matrix_non_basic_variable = numpy.zeros((dimension_m_of_A, dimension_n_of_A), dtype=float)
        for i in range(dimension_m_of_A):
            matrix_basic_variable[:, i] = matrix_A[:, vector_basic_variable_index[i]].copy()
        for i in range(dimension_n_of_A):
            matrix_non_basic_variable[:, i] = matrix_A[:, vector_non_basic_variable_index[i]].copy()
        # print("B = ")
        # print(matrix_basic_variable)
        # print("N = ")
        # print(matrix_non_basic_variable)

        vector_C_basic_variable = numpy.zeros((dimension_m_of_A), dtype=float)
        vector_C_non_basic_variable = numpy.zeros((dimension_n_of_A), dtype=float)
        for i in range(dimension_m_of_A):
            vector_C_basic_variable[i] = vector_C[vector_basic_variable_index[i]]
        for i in range(dimension_n_of_A):
            vector_C_non_basic_variable[i] = vector_C[vector_non_basic_variable_index[i]]
        # print("C(b) = ")
        # print(vector_C_basic_variable)
        # print("C(n) = ")
        # print(vector_C_non_basic_variable)

        vector_X_basic_variable = numpy.matmul(numpy.linalg.inv(matrix_basic_variable), vector_B.transpose())
        # print('X(b) = ')
        # print(vector_X_basic_variable)

        vector_Y = numpy.matmul(vector_C_basic_variable, numpy.linalg.inv(matrix_basic_variable))
        # print('y = ')
        # print(vector_Y)
        # print('Objective Value : ' + str(numpy.matmul(vector_Y, vector_B.transpose())))
        Objective_value = -1 * numpy.matmul(vector_Y, vector_B.transpose())

        if(Objective_value == previous_Objective_value):
            return 0
            
        previous_Objective_value = Objective_value

        vector_C_non_basic_variable_new = vector_C_non_basic_variable - numpy.matmul(vector_Y, matrix_non_basic_variable)

    if(is_unbounded == 0):
        print('Maximized Objective Value : ' + str(Objective_value))
        x = numpy.zeros((dimension_n_of_A), dtype=float)
        for i in range(dimension_n_of_A):
            if i in vector_basic_variable_index:
                x[i] = vector_X_basic_variable[numpy.where(vector_basic_variable_index == i)[0][0]]
        print('Value of vector X for maximized objective : ')
        print(x)
        return 1

##################################################################################
# Input for the mentioned problem

dimension_m_of_A = int(input("Please input the dimension m of matrix A(m * n matrix): "))      # dimension m of matrix A
while(dimension_m_of_A <= 0):
    print("Error!!!!! dimension m of matrix A cannot be zero or negative integer.")
    dimension_m_of_A = int(input("Please input the dimension m again : "))

dimension_n_of_A = int(input("Please input the dimension n of matrix A(m * n matrix): "))      # dimension n of matrix A
while(dimension_n_of_A <= 0):
    print("Error!!!!! dimension n of matrix A cannot be zero or negative integer.")
    dimension_n_of_A = int(input("Please input the dimension n again : "))

dimension_of_B = dimension_m_of_A

dimension_of_C = dimension_n_of_A

matrix_A = numpy.zeros((dimension_m_of_A, dimension_n_of_A), dtype=float)
print("Please input the elements of the matrix A : ")
for i in range(dimension_m_of_A):
    for j in range(dimension_n_of_A):
        matrix_A[i][j] = float(input("Input the element A[" + str(i) + "][" + str(j) + "] : "))

while(not numpy.any(matrix_A)):
    print("Error!!!!! matrix A cannot be a zero matrix.")
    print("Please input the elements of the matrix A again : ")
    for i in range(dimension_m_of_A):
        for j in range(dimension_n_of_A):
            matrix_A[i][j] = float(input("Input the element A[" + str(i) + "][" + str(j) + "] : "))

vector_B = numpy.zeros((dimension_of_B), dtype=float)
print("Please input the elements of the vector B : ")
for i in range(dimension_of_B):
    vector_B[i] = float(input("Input the element B[" + str(i) + "] : "))

vector_C = numpy.zeros((dimension_of_C), dtype=float)
print("Please input the elements of the vector C : ")
for i in range(dimension_of_C):
    vector_C[i] = float(input("Input the element C[" + str(i) + "] : "))

is_degenerate = Simplex_method(dimension_m_of_A, dimension_n_of_A, matrix_A, vector_B, vector_C)

while(is_degenerate == 0):
    for i in range(dimension_of_C):
        vector_C[i] = vector_C[i] + (numpy.random.randint(1, 10**4) / float(10.0**10))
    
    is_degenerate = Simplex_method(dimension_m_of_A, dimension_n_of_A, matrix_A, vector_B, vector_C)
