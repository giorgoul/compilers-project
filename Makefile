.PHONY: all clean part1 part2

all: part1 part2

part1:
	$(MAKE) -C part1/calculator compile

part2:
	$(MAKE) -C part2 compile

clean:
	$(MAKE) -C part1/calculator clean
	$(MAKE) -C part2 clean