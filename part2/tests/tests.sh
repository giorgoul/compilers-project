#!/bin/bash

GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

TEST_DIR="tests"
TEST_FILE="$TEST_DIR/test_list.txt"

total=0
failed=0


while IFS=$'\t ' read -r filename expected; do
    ((total++))
    expected_output=$(echo -e "$expected")

    make execute < "$TEST_DIR/$filename" > /dev/null 2>&1
    status=$?

    if [[ $status -ne 0 ]]; then
        if [[ "$expected_output" == "Parse Error" ]]; then
            echo -e "${GREEN}[OK]${NC} $filename"
        else
            echo -e "${RED}[FAIL]${NC} $filename"
            echo -e "=== Expected ===\n$expected_output"
            echo -e "\n=== Got ===\nParse Error"
            echo
            ((failed++))
        fi
    else
        actual_output=$(make run)
        if [[ "$actual_output" == "$expected_output" ]]; then
            echo -e "${GREEN}[OK]${NC} $filename"
        else
            echo -e "${RED}[FAIL]${NC} $filename"
            echo -e "=== Expected ===\n$expected_output"
            echo -e "\n=== Got ===\n$actual_output"
            echo
            ((failed++))
        fi
    fi
done < "$TEST_FILE"

echo
if [[ $failed -eq 0 ]]; then
    echo -e "${GREEN}All tests passed!${NC}"
else
    echo -e "${RED}Tests Failed: $failed/$total${NC}"
fi
