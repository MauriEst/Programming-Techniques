"""
Mauricio Estrella - CIS5511 - Homework 1
"""
## insertion sort
def insertion_sort(arr):
    A = list(arr) 
    comparisons = 0
    assignments = 0

    for i in range(1, len(A)):
        key = A[i]
        assignments += 1 
        
        j = i - 1
        
        while j >= 0 and A[j] > key:
            comparisons += 1 # compared A[i] with key
            A[j+ 1] = A[j]
            assignments += 1 # moved an element
            j = j - 1

        if j >= 0:
            comparisons += 1
            
        A[j + 1] = key
        assignments += 1
        
    return A, comparisons, assignments

# merge sort
def merge_sort(arr):
    A = list(arr)
    sorted_A, comparisons, assignments = _merge_sort_recursive(A, 0, len(A) - 1)
    return sorted_A, comparisons, assignments

def _merge_sort_recursive(A, p, r):
    comparisons = 0
    assignments = 0
    if p < r:
        q = (p + r) // 2
        left_sorted, left_comps, left_assigns = _merge_sort_recursive(A, p, q)
        right_sorted, right_comps, right_assigns = _merge_sort_recursive(A, q + 1, r)
        
        # the counts from the recursive calls
        comparisons += left_comps + right_comps
        assignments += left_assigns + right_assigns
        
        merged_A, merge_comps, merge_assigns = _merge(A, p, q, r)
        comparisons += merge_comps
        assignments += merge_assigns
        
    return A, comparisons, assignments

def _merge(A, p, q, r):
    comparisons = 0
    assignments = 0
    
    n1 = q - p + 1
    n2 = r - q
    
    L = A[p : p + n1]
    R = A[q + 1 : q + 1 + n2]
    
    i = 0
    j = 0
    k = p
    
    while i < n1 and j < n2:
        comparisons += 1 # the main comparison
        if L[i] <= R[j]:
            A[k] = L[i]
            assignments += 1
            i += 1
        else:
            A[k] = R[j]
            assignments += 1
            j += 1
        k += 1
        
    while i < n1:
        A[k] = L[i]
        assignments += 1
        i += 1
        k += 1

    while j < n2:
        A[k] = R[j]
        assignments += 1
        j += 1
        k += 1
        
    return A, comparisons, assignments

"""
Testing
"""
import random

def generate_random_list(size, min_val=0, max_val=100000):
    """generates a list of n size with random integers."""
    return [random.randint(min_val, max_val) for _ in range(size)]

def run_experiment():
    # input sizes to test
    sizes_to_test = [100, 500, 1000, 2500, 5000]
    
    print(f"{'Input Size':<12} | {'Algorithm':<15} | {'Comparisons':<15} | {'Assignments':<15}")
    print("-" * 65)

    for size in sizes_to_test:
        random_list = generate_random_list(size)
        
        # test Insertion Sort
        # pass a copy [:] so the original list isn't changed
        sorted_is, is_comps, is_assigns = insertion_sort(random_list[:])
        print(f"{size:<12} | {'Insertion Sort':<15} | {is_comps:<15} | {is_assigns:<15}")
        
        # test Merge Sort
        sorted_ms, ms_comps, ms_assigns = merge_sort(random_list[:])
        print(f"{size:<12} | {'Merge Sort':<15} | {ms_comps:<15} | {ms_assigns:<15}")
        print("-" * 65)

    print("--- Starting Sample Run for Insertion Sort ---")
    sample_list = [23, 3, 10, 2]
    print(f"Original list: {sample_list}")

    sorted_list, comparisons, assignments = insertion_sort(sample_list)

    print(f"Sorted list: {sorted_list}")
    print(f"Total comparisons: {comparisons}")
    print(f"Total assignments: {assignments}")
    print("--- End of Sample Run ---")

    print("--- Starting Sample Run for Merge Sort ---")
    sample_list = [23, 3, 10, 2, 55, 14]
    print(f"Original list: {sample_list}")

    sorted_list, comparisons, assignments = merge_sort(sample_list)

    print(f"Sorted list: {sorted_list}")
    print(f"Total comparisons: {comparisons}")
    print(f"Total assignments: {assignments}")
    print("--- End of Sample Run ---")

run_experiment()