const fs = require('fs');
const path = require('path');

const srcDir = 'D:/workspace/med-text-analytics/med-text-analytics-processor/src/main/java/br/com/fiap/techchallenge/processor';
const domainDir = path.join(srcDir, 'domain');
const entityDir = path.join(srcDir, 'persistence/entity');
const mapperDir = path.join(srcDir, 'persistence/mapper');

// Helper to walk directory
function walkSync(dir, filelist = []) {
  fs.readdirSync(dir).forEach(file => {
    const dirFile = path.join(dir, file);
    if (fs.statSync(dirFile).isDirectory()) {
      filelist = walkSync(dirFile, filelist);
    } else {
      filelist.push(dirFile);
    }
  });
  return filelist;
}

const javaFiles = walkSync(domainDir).filter(f => f.endsWith('.java'));
const classes = [];

javaFiles.forEach(file => {
  const fileName = path.basename(file);
  if (fileName === 'DocumentType.java') return;
  
  const className = fileName.replace('.java', '');
  const relativePath = path.relative(domainDir, file);
  const subPackageStr = path.dirname(relativePath);
  const subPackage = subPackageStr === '.' ? '' : subPackageStr.replace(/\\/g, '.').replace(/\//g, '.');
  
  classes.push({
    name: className,
    subPackage: subPackage,
    file: file
  });
});

console.log('Found classes:', classes.map(c => c.name));

const domainClassNames = classes.map(c => c.name);

function replaceDomainUsages(content) {
  let newContent = content;
  domainClassNames.forEach(cls => {
    const importRegex = new RegExp(`import br\\.com\\.fiap\\.techchallenge\\.processor\\.domain\\.(.*?)\\b${cls};`, 'g');
    newContent = newContent.replace(importRegex, `import br.com.fiap.techchallenge.processor.persistence.entity.$1${cls}Entity;`);
    
    const importRegex2 = new RegExp(`import br\\.com\\.fiap\\.techchallenge\\.processor\\.domain\\.${cls};`, 'g');
    newContent = newContent.replace(importRegex2, `import br.com.fiap.techchallenge.processor.persistence.entity.${cls}Entity;`);
    
    const typeRegex = new RegExp(`\\b${cls}\\b`, 'g');
    newContent = newContent.replace(typeRegex, `${cls}Entity`);
  });
  return newContent;
}

if (!fs.existsSync(entityDir)) fs.mkdirSync(entityDir, { recursive: true });
if (!fs.existsSync(mapperDir)) fs.mkdirSync(mapperDir, { recursive: true });

classes.forEach(cls => {
  const content = fs.readFileSync(cls.file, 'utf-8');
  
  let entityContent = content;
  const pkgRegex = /package br\.com\.fiap\.techchallenge\.processor\.domain(.*?);/;
  entityContent = entityContent.replace(pkgRegex, 'package br.com.fiap.techchallenge.processor.persistence.entity$1;');
  entityContent = replaceDomainUsages(entityContent);
  
  const entitySubDir = cls.subPackage ? path.join(entityDir, cls.subPackage.replace(/\./g, '/')) : entityDir;
  if (!fs.existsSync(entitySubDir)) fs.mkdirSync(entitySubDir, { recursive: true });
  fs.writeFileSync(path.join(entitySubDir, `${cls.name}Entity.java`), entityContent);
  
  let domainContent = content;
  domainContent = domainContent.replace(/extends\s+PanacheMongoEntity\s*/, '');
  domainContent = domainContent.replace(/import\s+io\.quarkus\.mongodb\.panache.*?;[\r\n]*/g, '');
  domainContent = domainContent.replace(/@MongoEntity(\(.*?\))?[\r\n]*/g, '');
  if (cls.name === 'Document') {
      domainContent = domainContent.replace('public abstract class Document {', 'import org.bson.types.ObjectId;\n\npublic abstract class Document {\n\n    private ObjectId id;');
  }
  fs.writeFileSync(cls.file, domainContent);
  
  const mapperSubDir = cls.subPackage ? path.join(mapperDir, cls.subPackage.replace(/\./g, '/')) : mapperDir;
  if (!fs.existsSync(mapperSubDir)) fs.mkdirSync(mapperSubDir, { recursive: true });
  
  const mapperPkg = cls.subPackage ? `br.com.fiap.techchallenge.processor.persistence.mapper.${cls.subPackage}` : 'br.com.fiap.techchallenge.processor.persistence.mapper';
  const entityPkg = cls.subPackage ? `br.com.fiap.techchallenge.processor.persistence.entity.${cls.subPackage}` : 'br.com.fiap.techchallenge.processor.persistence.entity';
  const domainPkg = cls.subPackage ? `br.com.fiap.techchallenge.processor.domain.${cls.subPackage}` : 'br.com.fiap.techchallenge.processor.domain';
  
  const mapperContent = `package ${mapperPkg};

import ${entityPkg}.${cls.name}Entity;
import ${domainPkg}.${cls.name};
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ${cls.name}Mapper {
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static ${cls.name}Entity toEntity(${cls.name} domain) {
        if (domain == null) return null;
        return objectMapper.convertValue(domain, ${cls.name}Entity.class);
    }

    public static ${cls.name} toDomain(${cls.name}Entity entity) {
        if (entity == null) return null;
        return objectMapper.convertValue(entity, ${cls.name}.class);
    }
}
`;
  fs.writeFileSync(path.join(mapperSubDir, `${cls.name}Mapper.java`), mapperContent);
});

const allSrcFiles = walkSync(srcDir).filter(f => f.endsWith('.java'));
let updatedCount = 0;
allSrcFiles.forEach(file => {
  if (file.includes(path.normalize('processor/domain')) || 
      file.includes(path.normalize('processor/persistence/entity')) ||
      file.includes(path.normalize('processor/persistence/mapper'))) {
      return;
  }
  
  const content = fs.readFileSync(file, 'utf-8');
  const newContent = replaceDomainUsages(content);
  if (content !== newContent) {
    fs.writeFileSync(file, newContent);
    updatedCount++;
  }
});
console.log(`Updated ${updatedCount} files outside domain/entity/mapper.`);
