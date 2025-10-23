"""
Mauricio Estrella - CIS5511 - Homework 2
"""
def binary_search(arr, x):
    low = 0
    high = len(arr) - 1 # [1,2,3,4,5]
    comparisons = 0
    while low <= high:
        comparisons += 1 # main comparison done here
        mid = (low + high) // 2
        if arr[mid] == x:
            return mid, comparisons
        elif arr[mid] < x:
            low = mid + 1
        else:
            high = mid - 1
    return -1, comparisons

"""
Testing
"""
import random
import math

def generate_random_list(size, min_val=0, max_val=10000):
    """generates a list of n size with random integers."""
    return sorted([random.randint(min_val, max_val) for _ in range(size)])

def run_experiment():
    # input sizes to test
    sizes_to_test = [100, 500, 1000, 5000, 10000]
    
    print(f"{'Input Size (n)':<15} | {'Avg Comparisons':<20} | {'Theoretical (log2 n)':<15}")
    print("-" * 65)

    for size in sizes_to_test:
        random_list = generate_random_list(size)
        total_comparisons = 0
        
        # # test Binary Search
        # x = binary_search(random_list, random.randint(0,10000))
        # print(f"Binary search found x at index: {x}")

        for key_to_find in random_list:
            _, comparisons = binary_search(random_list, key_to_find)
            total_comparisons += comparisons
            
        average_comparisons = total_comparisons / size
        theoretical_avg = math.log2(size)
        
        print(f"{size:<15} | {average_comparisons:<20.2f} | {theoretical_avg:<20.2f}")

    sorted_list = [2, 3, 10, 14, 23, 55, 89, 99]
    x = 10

    print("--- Starting Sample Run for Binary Search ---")
    print(f"Sorted list to search in: {sorted_list}")
    print(f"Key to find: {x}")
    index, comparisons = binary_search(sorted_list, x)
    print(f"Result: Found at index {index}")
    print(f"Total comparisons: {comparisons}")
    print("--- End of Sample Run ---")

    sorted_list = [2, 3, 10, 14, 23, 55, 89, 99, 207, 308, 500]
    x = 500

    print("--- Starting Sample Run for Binary Search ---")
    print(f"Sorted list to search in: {sorted_list}")
    print(f"Key to find: {x}")
    index, comparisons = binary_search(sorted_list, x)
    print(f"Result: Found at index {index}")
    print(f"Total comparisons: {comparisons}")
    print("--- End of Sample Run ---")

run_experiment()