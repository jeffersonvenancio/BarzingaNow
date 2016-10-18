def find_prop(prop):
	props = {}
	cfg_file = open('config.cfg')
	for line in cfg_file:
		l = line.strip()
		if l and not l.startswith('#'):
			key_value = l.split('==')
			key = key_value[0].strip()

			if key == prop:
				value = '=='.join(key_value[1:]).strip().strip('"')
				return value